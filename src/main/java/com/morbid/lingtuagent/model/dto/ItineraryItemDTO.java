package com.morbid.lingtuagent.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItineraryItemDTO {
    @NotBlank(message = "项目类型不能为空")
    private String itemType;
    @NotNull(message = "项目ID不能为空")
    private Long itemId;
    private Integer sortOrder;
    private String note;
}
