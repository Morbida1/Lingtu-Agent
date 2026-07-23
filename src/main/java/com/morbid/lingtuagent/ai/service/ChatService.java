package com.morbid.lingtuagent.ai.service;

import com.morbid.lingtuagent.ai.model.vo.ChatMessageVO;
import com.morbid.lingtuagent.ai.model.vo.ChatSessionVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface ChatService {
/*
发送消息（SSE 流式返回）
@param userId    当前用户ID
@param sessionId 会话ID（null 则自动创建新会话）
@param message   用户消息
@return SseEmitter 流式响应
*/
    SseEmitter chat(Long userId,Long sessionId,String message);
    //创建新会话
    ChatSessionVO createChatSession(Long userId,String title);
    //获取用户会话列表
    List<ChatSessionVO> listChatSession(Long userId);
    //获取会话消息列表
    List<ChatMessageVO> getMessages(Long sessionId);
    //删除会话
    void deleteChatSession(Long sessionId);
    //使用 Chat Agent 聊天（带工具调用）
    SseEmitter chatWithAgent(Long userId, Long sessionId, String message);
}
