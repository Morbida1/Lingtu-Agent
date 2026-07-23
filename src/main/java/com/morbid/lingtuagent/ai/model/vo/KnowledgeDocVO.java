package com.morbid.lingtuagent.ai.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KnowledgeDocVO {
    private Long id;
    private String title;
    private String fileType;
    private Integer chunkCount;
    private Integer status;
    private String errorMsg;
    private LocalDateTime createTime;
}
