package com.morbid.lingtuagent.ai.tool;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.morbid.lingtuagent.model.entity.City;
import com.morbid.lingtuagent.service.CityService;
import com.morbid.lingtuagent.service.HotelService;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.P;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HotelSearchTool {

    private final HotelService hotelService;
    private final CityService cityService;

    @Tool("根据城市名称查询酒店信息")
    public String searchHotels(
            @P("城市名称") String cityName,
            @P("价格范围，如：便宜、中等、昂贵") String priceRange) {
        City city = cityService.getOne(new LambdaQueryWrapper<City>().eq(City::getName, cityName));
        if (city == null) return "未找到城市：" + cityName;
        return hotelService.listByCityId(city.getId()).toString();
    }
}