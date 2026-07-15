package com.morbid.lingtuagent.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SpotDTO {
    @NotBlank(message = "景点名称不能为空")
    private String name;
    private Long cityId;
    private String description;
    private String address;
    private String imageUrl;
    private BigDecimal rating;
    private BigDecimal price;
    private String openingHours;
    private String tags;
}
