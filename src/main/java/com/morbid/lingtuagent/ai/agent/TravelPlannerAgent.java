package com.morbid.lingtuagent.ai.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface TravelPlannerAgent {

    @SystemMessage("""
        你是一个专业的旅行规划师。
        根据用户的需求，调用相关工具收集信息，然后生成详细的旅行计划。
        
        计划应包括：
        1. 每日详细行程（景点、餐饮、交通）
        2. 推荐酒店
        3. 预算估算
        4. 注意事项和建议
        
        请确保计划合理、详细、实用。
        """)
    @UserMessage("{{userRequest}}")
    String planTrip(@V("userRequest") String userRequest);
}