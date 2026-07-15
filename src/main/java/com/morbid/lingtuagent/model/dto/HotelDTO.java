package com.morbid.lingtuagent.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class HotelDTO {
    @NotBlank(message = "酒店名不能为空")
    private String name;
    private String description;
    private String address;
    private String imageUrl;
    private BigDecimal rating;
    private Integer starRating;
    private String priceRange;
    private String facilities;
    private String contactPhone;

}