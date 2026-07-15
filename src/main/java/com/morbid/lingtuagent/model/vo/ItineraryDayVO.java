package com.morbid.lingtuagent.model.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;


@Data
public class ItineraryDayVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private Integer dayNumber;
    private String description;
    private List<ItineraryItemVO> itemList;
}

