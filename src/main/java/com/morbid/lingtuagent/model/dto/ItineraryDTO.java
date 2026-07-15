package com.morbid.lingtuagent.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ItineraryDTO {
    @NotBlank(message = "标题不能为空")
    private String title;
    @NotNull(message = "城市ID不能为空")
    private Long cityId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer days;
    private BigDecimal budget;
    private String status;
    private String description;
    private String content;
    @Valid
    private List<ItineraryDayDTO> dayList;
}