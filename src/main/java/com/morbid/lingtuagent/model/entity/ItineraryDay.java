package com.morbid.lingtuagent.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("itinerary_day")
public class ItineraryDay {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long itineraryId;
    private Integer dayNumber;
    private String description;

    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

