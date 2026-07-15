package com.morbid.lingtuagent.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("city")
public class City {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String province;
    private String description;
    private String imageUrl;
    private Integer sortOrder;

    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
