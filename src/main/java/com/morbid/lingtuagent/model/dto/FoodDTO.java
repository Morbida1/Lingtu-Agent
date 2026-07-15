package com.morbid.lingtuagent.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FoodDTO {
    @NotBlank(message = "美食名不能为空")
    private String name;
    private Long cityId;
    private String description;
    private String address;
    private String imageUrl;
    private BigDecimal rating;
    private String priceRange;
    private String category;
}
