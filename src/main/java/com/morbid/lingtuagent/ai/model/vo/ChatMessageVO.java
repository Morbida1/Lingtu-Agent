package com.morbid.lingtuagent.ai.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageVO {
    private Long id;
    private Long sessionId;
    private Long userId;
    private String role;
    private String content;
    private LocalDateTime createTime;
}
