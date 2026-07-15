package com.morbid.lingtuagent.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("itinerary")
public class Itinerary {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private Long cityId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer days;
    private BigDecimal budget;
    private String status;
    private String description;
    private String content;

    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
