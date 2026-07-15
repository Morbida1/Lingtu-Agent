package com.morbid.lingtuagent.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FoodVO {
    private Long id;
    private String name;
    private Long cityId;
    private String description;
    private String address;
    private String imageUrl;
    private BigDecimal rating;
    private String priceRange;
    private String category;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
