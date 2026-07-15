package com.morbid.lingtuagent.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("itinerary_item")
public class ItineraryItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long itineraryDayId;
    private String itemType;
    private Long itemId;
    private Integer sortOrder;
    private String note;

    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
