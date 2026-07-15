package com.morbid.lingtuagent.model.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class ItineraryItemVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String itemType;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long itemId;
    private Integer sortOrder;
    private String note;
}