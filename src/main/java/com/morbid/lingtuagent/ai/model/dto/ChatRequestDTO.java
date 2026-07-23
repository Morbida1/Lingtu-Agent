package com.morbid.lingtuagent.ai.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequestDTO {
    /*
     会话ID，传 null 则自动创建新会话
     */
    private Long sessionId;
    @NotBlank(message = "消息内容不能为空")
    private String message;
}
