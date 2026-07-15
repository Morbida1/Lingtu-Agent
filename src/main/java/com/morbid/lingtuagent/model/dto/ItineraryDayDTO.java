package com.morbid.lingtuagent.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ItineraryDayDTO {
    @NotNull(message = "天数序号不能为空")
    private Integer dayNumber;
    private String description;
    @Valid
    private List<ItineraryItemDTO> itemList;
}
