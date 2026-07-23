package com.morbid.lingtuagent.ai.tool;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.P;
import org.springframework.stereotype.Component;

@Component
public class BudgetTool {

    @Tool("计算旅行预算")
    public String calculateBudget(
            @P("城市名称") String cityName,
            @P("旅行天数") int days,
            @P("人数") int people) {

        int dailyCost = switch (cityName) {
            case "北京", "上海", "深圳" -> 800;
            case "重庆", "成都", "西安" -> 500;
            default -> 600;
        };

        int total = dailyCost * days * people;
        int hotel = 300 * days * people;
        int food = 150 * days * people;
        int transport = 100 * days * people;
        int tickets = 200 * days * people;

        return String.format("""
            %s %d天%d人旅行预算估算：
            - 酒店：约 %d 元
            - 餐饮：约 %d 元
            - 交通：约 %d 元
            - 门票：约 %d 元
            - 总计：约 %d 元
            """, cityName, days, people, hotel, food, transport, tickets, total);
    }
}