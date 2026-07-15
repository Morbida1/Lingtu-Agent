package com.morbid.lingtuagent.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CityVO {
    // ID 返回给前端时转为 String，避免前端精度丢失
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String name;
    private String province;
    private String description;
    private String imageUrl;
    private Integer sortOrder;
    // 统一日期格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
