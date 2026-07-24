package com.morbid.lingtuagent.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("hotel")
public class Hotel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long cityId;
    private String description;
    private String address;
    private String imageUrl;
    private BigDecimal rating;
    private Integer starRating;
    private String priceRange;
    private String facilities;
    private String contactPhone;

    @TableLogic(value = "0", delval = "1") // 逻辑删除标记
    private Integer deleted;

    private LocalDateTime deleteTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}