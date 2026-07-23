package com.morbid.lingtuagent.ai.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.morbid.lingtuagent.ai.agent.TravelAssistant;
import com.morbid.lingtuagent.ai.mapper.ChatMessageMapper;
import com.morbid.lingtuagent.ai.mapper.ChatSessionMapper;
import com.morbid.lingtuagent.ai.model.entity.ChatMessage;
import com.morbid.lingtuagent.ai.model.entity.ChatSession;
import com.morbid.lingtuagent.ai.model.vo.ChatMessageVO;
import com.morbid.lingtuagent.ai.model.vo.ChatSessionVO;
import com.morbid.lingtuagent.ai.service.ChatService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl extends ServiceImpl<ChatSessionMapper, ChatSession> implements ChatService {
    private final ChatSessionMapper chatSessionMapper;
    private final ChatMessageMapper chatMessageMapper;
    private final StreamingChatModel streamingChatModel;
    private final TravelAssistant travelAssistant;

    //系统提示词：定制AI为灵途旅行助手
    private static final String SYSTEM_PROMPT = """
            你是一个专业的旅行助手，名叫"灵途"。你可以帮助用户：
            1. 推荐旅行目的地和景点
            2. 规划行程路线
            3. 推荐当地美食和酒店
            4. 提供旅行建议和注意事项
            请用热情、友好的语气回答，回答简洁有条理。
            """;
    //历史记录限制：最多保留10条消息,防止token超量
    private static final int HISTORY_LIMIT = 10;

    // 存储正在进行的流式响应，用于异常处理
    private final Map<String, StringBuilder> streamingResponses = new ConcurrentHashMap<>();

    @Override
    public SseEmitter chat(Long userId, Long sessionId, String message) {
        SseEmitter emitter = new SseEmitter(120000L);
        StringBuilder fullResponse = new StringBuilder();
        String responseKey = userId + "_" + (sessionId != null ? sessionId : "new");

        // 设置SSE回调
        emitter.onCompletion(() -> {
            log.info("SSE连接完成: {}", responseKey);
            streamingResponses.remove(responseKey);
        });

        emitter.onTimeout(() -> {
            log.warn("SSE连接超时: {}", responseKey);
            streamingResponses.remove(responseKey);
        });

        emitter.onError((e) -> {
            log.error("SSE连接错误: {}", responseKey, e);
            streamingResponses.remove(responseKey);
        });

        streamingResponses.put(responseKey, fullResponse);

        // 异步处理AI请求
        new Thread(() -> {
            try {
                // 1. 获取或创建会话
                Long actualSessionId = getOrCreateSession(userId, sessionId);

                // 2. 保存用户消息
                saveUserMessage(userId, actualSessionId, message);

                // 3. 加载历史记录
                List<com.morbid.lingtuagent.ai.model.entity.ChatMessage> historyMessages = loadHistory(actualSessionId);

                // 4. 构建AI消息列表
                List<dev.langchain4j.data.message.ChatMessage> aiMessages = buildAiMessages(historyMessages, message);

                // 5. 流式调用AI模型
                streamingChatModel.chat(aiMessages, new StreamingChatResponseHandler() {
                    @Override
                    public void onPartialResponse(String token) {
                        try {
                            fullResponse.append(token);
                            emitter.send(SseEmitter.event()
                                    .name("message")
                                    .data(token));
                        } catch (IOException e) {
                            log.error("发送SSE消息失败", e);
                        }
                    }

                    @Override
                    public void onCompleteResponse(ChatResponse response) {
                        try {
                            // 6. 保存AI回复
                            saveAiMessage(userId, actualSessionId, fullResponse.toString());
                            log.info("AI回复完成，长度: {}", fullResponse.length());
                            emitter.send(SseEmitter.event()
                                    .name("done")
                                    .data("[DONE]"));
                            emitter.complete();
                        } catch (IOException e) {
                            log.error("发送SSE完成事件失败", e);
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        log.error("AI流式调用失败", error);
                        try {
                            emitter.send(SseEmitter.event()
                                    .name("error")
                                    .data("AI服务调用失败: " + error.getMessage()));
                            emitter.completeWithError(error);
                        } catch (IOException e) {
                            log.error("发送SSE错误事件失败", e);
                        }
                    }
                });

            } catch (Exception e) {
                log.error("处理聊天请求失败", e);
                try {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("处理请求失败: " + e.getMessage()));
                    emitter.completeWithError(e);
                } catch (IOException ex) {
                    log.error("发送错误响应失败", ex);
                }
            }
        }).start();

        return emitter;
    }

    @Override
    public ChatSessionVO createChatSession(Long userId, String title) {
        ChatSession session = new ChatSession();
        session.setUserId(userId);
        session.setTitle(title != null ? title : "新对话");
        session.setCreateTime(LocalDateTime.now());
        session.setUpdateTime(LocalDateTime.now());
        chatSessionMapper.insert(session);

        ChatSessionVO vo = new ChatSessionVO();
        BeanUtils.copyProperties(session, vo);
        log.info("创建聊天会话成功: userId={}, sessionId={}", userId, session.getId());
        return vo;
    }

    @Override
    public List<ChatSessionVO> listChatSession(Long userId) {
        LambdaQueryWrapper<ChatSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatSession::getUserId, userId)
                .orderByDesc(ChatSession::getUpdateTime);

        List<ChatSession> sessions = chatSessionMapper.selectList(wrapper);
        return sessions.stream()
                .map(session -> {
                    ChatSessionVO vo = new ChatSessionVO();
                    BeanUtils.copyProperties(session, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }
    @Override
    public List<ChatMessageVO> getMessages(Long sessionId) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getSessionId, sessionId)
                .orderByAsc(ChatMessage::getCreateTime);
        List<ChatMessage> messages = chatMessageMapper.selectList(wrapper);
        return messages.stream()
                .map(message -> {
                    ChatMessageVO vo = new ChatMessageVO();
                    BeanUtils.copyProperties(message, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteChatSession(Long sessionId) {
        // 删除会话
        chatSessionMapper.deleteById(sessionId);
        // 删除该会话下的所有消息
        LambdaQueryWrapper<com.morbid.lingtuagent.ai.model.entity.ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(com.morbid.lingtuagent.ai.model.entity.ChatMessage::getSessionId, sessionId);
        chatMessageMapper.delete(wrapper);
        log.info("删除聊天会话成功: sessionId={}", sessionId);
    }

    //获取或创建会话
    private Long getOrCreateSession(Long userId, Long sessionId) {
        if (sessionId != null) {
            ChatSession session = chatSessionMapper.selectById(sessionId);
            if (session != null && session.getUserId().equals(userId)) {
                return sessionId;
            }
        }
        // 创建新会话
        ChatSessionVO newSession = createChatSession(userId, null);
        return newSession.getId();
    }

    //保存用户消息
    private void saveUserMessage(Long userId, Long sessionId, String message) {
        com.morbid.lingtuagent.ai.model.entity.ChatMessage chatMessage = new com.morbid.lingtuagent.ai.model.entity.ChatMessage();
        chatMessage.setUserId(userId);
        chatMessage.setSessionId(sessionId);
        chatMessage.setRole("user");
        chatMessage.setContent(message);
        chatMessage.setCreateTime(LocalDateTime.now());
        chatMessageMapper.insert(chatMessage);
    }

    /**
     * 保存AI回复
     */
    private void saveAiMessage(Long userId, Long sessionId, String content) {
        com.morbid.lingtuagent.ai.model.entity.ChatMessage chatMessage = new com.morbid.lingtuagent.ai.model.entity.ChatMessage();
        chatMessage.setUserId(userId);
        chatMessage.setSessionId(sessionId);
        chatMessage.setRole("assistant");
        chatMessage.setContent(content);
        chatMessage.setCreateTime(LocalDateTime.now());
        chatMessageMapper.insert(chatMessage);
    }

    /**
     * 加载历史消息（最多HISTORY_LIMIT条）
     */
    private List<com.morbid.lingtuagent.ai.model.entity.ChatMessage> loadHistory(Long sessionId) {
        LambdaQueryWrapper<com.morbid.lingtuagent.ai.model.entity.ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(com.morbid.lingtuagent.ai.model.entity.ChatMessage::getSessionId, sessionId)
                .orderByAsc(com.morbid.lingtuagent.ai.model.entity.ChatMessage::getCreateTime)
                .last("LIMIT " + HISTORY_LIMIT);
        return chatMessageMapper.selectList(wrapper);
    }

    @Override
    public SseEmitter chatWithAgent(Long userId, Long sessionId, String message) {
        SseEmitter emitter = new SseEmitter(300000L);
        String responseKey = userId + "_agent_" + (sessionId != null ? sessionId : "new");

        emitter.onCompletion(() -> {
            log.info("Agent SSE连接完成: {}", responseKey);
            streamingResponses.remove(responseKey);
        });

        emitter.onTimeout(() -> {
            log.warn("Agent SSE连接超时: {}", responseKey);
            streamingResponses.remove(responseKey);
        });

        emitter.onError((e) -> {
            log.error("Agent SSE连接错误: {}", responseKey, e);
            streamingResponses.remove(responseKey);
        });

        new Thread(() -> {
            try {
                Long actualSessionId = getOrCreateSession(userId, sessionId);

                saveUserMessage(userId, actualSessionId, message);

                List<com.morbid.lingtuagent.ai.model.entity.ChatMessage> historyMessages = loadHistory(actualSessionId);

                String agentInput = buildAgentInput(historyMessages, message);

                log.info("开始调用Agent，sessionId: {}", actualSessionId);
                String agentResponse = travelAssistant.chat(agentInput);
                log.info("Agent调用完成，响应长度: {}", agentResponse.length());

                saveAiMessage(userId, actualSessionId, agentResponse);

                for (int i = 0; i < agentResponse.length(); i++) {
                    emitter.send(SseEmitter.event()
                            .name("message")
                            .data(String.valueOf(agentResponse.charAt(i))));
                    Thread.sleep(15);
                }

                emitter.send(SseEmitter.event()
                        .name("done")
                        .data("[DONE]"));
                emitter.complete();

            } catch (Exception e) {
                log.error("Agent聊天处理失败", e);
                try {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("Agent服务调用失败: " + e.getMessage()));
                    emitter.completeWithError(e);
                } catch (IOException ex) {
                    log.error("发送Agent错误响应失败", ex);
                }
            }
        }).start();

        return emitter;
    }

    private String buildAgentInput(
            List<com.morbid.lingtuagent.ai.model.entity.ChatMessage> historyMessages,
            String currentMessage) {
        StringBuilder sb = new StringBuilder();
        for (com.morbid.lingtuagent.ai.model.entity.ChatMessage msg : historyMessages) {
            if ("user".equals(msg.getRole())) {
                sb.append("用户: ").append(msg.getContent()).append("\n");
            } else if ("assistant".equals(msg.getRole())) {
                sb.append("助手: ").append(msg.getContent()).append("\n");
            }
        }
        sb.append("用户: ").append(currentMessage);
        return sb.toString();
    }

    /**
     * 构建AI消息列表（包含系统提示词、历史记录和当前消息）
     */
    private List<dev.langchain4j.data.message.ChatMessage> buildAiMessages(
            List<com.morbid.lingtuagent.ai.model.entity.ChatMessage> historyMessages, String currentMessage) {
        List<dev.langchain4j.data.message.ChatMessage> messages = new ArrayList<>();

        // 添加系统提示词
        messages.add(SystemMessage.from(SYSTEM_PROMPT));

        // 添加历史消息
        for (com.morbid.lingtuagent.ai.model.entity.ChatMessage msg : historyMessages) {
            if ("user".equals(msg.getRole())) {
                messages.add(UserMessage.from(msg.getContent()));
            } else if ("assistant".equals(msg.getRole())) {
                messages.add(AiMessage.from(msg.getContent()));
            }
        }

        // 添加当前消息
        messages.add(UserMessage.from(currentMessage));

        return messages;
    }
}