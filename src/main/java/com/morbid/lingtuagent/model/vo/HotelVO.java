package com.morbid.lingtuagent.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class HotelVO {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String imageUrl;
    private BigDecimal rating;
    private Integer starRating;
    private String priceRange;
    private String facilities;
    private String contactPhone;
    // 统一日期格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
