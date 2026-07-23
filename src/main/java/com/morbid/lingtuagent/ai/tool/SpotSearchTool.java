package com.morbid.lingtuagent.ai.tool;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.morbid.lingtuagent.model.entity.City;
import com.morbid.lingtuagent.service.CityService;
import com.morbid.lingtuagent.service.SpotService;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.P;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpotSearchTool {

    private final SpotService spotService;
    private final CityService cityService;

    @Tool("根据城市名称查询景点信息")
    public String searchSpots(@P("城市名称") String cityName) {
        City city = cityService.getOne(new LambdaQueryWrapper<City>().eq(City::getName, cityName));
        if (city == null) return "未找到城市：" + cityName;
        return spotService.listByCityId(city.getId()).toString();
    }
}