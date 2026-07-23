package com.morbid.lingtuagent.ai.tool;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.P;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Random;

@Slf4j
@Component
public class WeatherTool {

    @Tool("查询指定城市的天气信息")
    public String getWeather(
            @P("城市名称") String cityName,
            @P("日期，格式：yyyy-MM-dd，可选") String date) {

        String queryDate = date != null ? date : LocalDate.now().toString();

        String[] weathers = {"晴", "多云", "小雨", "阴天"};
        String[] temps = {"15-25°C", "18-28°C", "12-20°C", "20-30°C"};

        Random random = new Random();
        String weather = weathers[random.nextInt(weathers.length)];
        String temp = temps[random.nextInt(temps.length)];

        return String.format("%s %s 天气：%s，温度：%s", cityName, queryDate, weather, temp);
    }
}