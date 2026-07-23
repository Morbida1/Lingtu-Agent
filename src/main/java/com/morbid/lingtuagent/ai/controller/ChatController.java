package com.morbid.lingtuagent.ai.controller;

import com.morbid.lingtuagent.ai.model.dto.ChatRequestDTO;
import com.morbid.lingtuagent.ai.model.vo.ChatMessageVO;
import com.morbid.lingtuagent.ai.model.vo.ChatSessionVO;
import com.morbid.lingtuagent.ai.service.ChatService;
import com.morbid.lingtuagent.common.Result;
import com.morbid.lingtuagent.common.ResultCode;
import com.morbid.lingtuagent.common.exception.BusinessException;
import com.morbid.lingtuagent.model.entity.User;
import com.morbid.lingtuagent.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final UserService userService;
    //流式聊天接口（SSE）
    @RequestMapping(value = "/send",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter send(@RequestBody ChatRequestDTO request) {
        Long userId = getCurrentUserId();
        return chatService.chat(userId, request.getSessionId(), request.getMessage());
    }

    //Agent工具调用聊天接口（SSE）
    @RequestMapping(value = "/send/agent",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatWithAgent(@RequestBody ChatRequestDTO request) {
        Long userId = getCurrentUserId();
        return chatService.chatWithAgent(userId, request.getSessionId(), request.getMessage());
    }
    //手动创建对话
    @PostMapping("/session")
    public Result<ChatSessionVO> createChatSession(@RequestParam(required = false) String title){
        Long userId = getCurrentUserId();
        return Result.success(chatService.createChatSession(userId,title));
    }
    //获取用户所有会话列表
    @GetMapping("/session")
    public Result<List<ChatSessionVO>> listChatSession() {
        Long userId = getCurrentUserId();
        return Result.success(chatService.listChatSession(userId));
    }
    //获取指定会话历史消息
    @GetMapping("/message/{sessionId}")
    public Result<List<ChatMessageVO>> listChatMessage(@PathVariable Long sessionId){
        return Result.success(chatService.getMessages(sessionId));
    }
    //删除会话及对应消息
    @DeleteMapping("/session/{sessionId}")
    public Result<Void> deleteChatSession(@PathVariable Long sessionId){
        chatService.deleteChatSession(sessionId);
        return Result.success();
    }
    //从SpringSecurity上下文获取当前登录用户ID
    private Long getCurrentUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || ! authentication.isAuthenticated()){
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return user.getId();
    }
}