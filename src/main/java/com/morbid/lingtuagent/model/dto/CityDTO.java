package com.morbid.lingtuagent.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CityDTO {
    @NotBlank(message = "城市名不能为空")
    private String name;
    private String province;
    private String description;
    private String imageUrl;
    private Integer sortOrder;
}
