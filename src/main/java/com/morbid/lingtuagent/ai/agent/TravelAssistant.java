package com.morbid.lingtuagent.ai.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface TravelAssistant {

    @SystemMessage("""
        你是一个专业的旅行助手，名叫"灵途"。
        你可以调用各种工具来帮助用户：
        - 查询酒店信息
        - 查询天气信息
        - 查询景点信息
        - 计算旅行预算
        
        请根据用户的需求，智能地调用相关工具，然后给出详细的回答。
        """)
    @UserMessage("{{userMessage}}")
    String chat(@V("userMessage") String userMessage);
}