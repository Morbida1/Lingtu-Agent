package com.morbid.lingtuagent.ai.config;

import com.morbid.lingtuagent.ai.agent.TravelAssistant;
import com.morbid.lingtuagent.ai.agent.TravelPlannerAgent;
import com.morbid.lingtuagent.ai.tool.BudgetTool;
import com.morbid.lingtuagent.ai.tool.HotelSearchTool;
import com.morbid.lingtuagent.ai.tool.SpotSearchTool;
import com.morbid.lingtuagent.ai.tool.WeatherTool;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfig {

    @Bean
    public TravelAssistant travelAssistant(
            ChatModel chatModel,
            HotelSearchTool hotelSearchTool,
            WeatherTool weatherTool,
            SpotSearchTool spotSearchTool,
            BudgetTool budgetTool) {

        return AiServices.builder(TravelAssistant.class)
                .chatModel(chatModel)
                .tools(hotelSearchTool, weatherTool, spotSearchTool, budgetTool)
                .build();
    }
    @Bean
    public TravelPlannerAgent travelPlannerAgent(
            ChatModel chatModel,
            HotelSearchTool hotelSearchTool,
            WeatherTool weatherTool,
            SpotSearchTool spotSearchTool,
            BudgetTool budgetTool) {

        return AiServices.builder(TravelPlannerAgent.class)
                .chatModel(chatModel)
                .tools(hotelSearchTool, weatherTool, spotSearchTool, budgetTool)
                .build();
    }
}