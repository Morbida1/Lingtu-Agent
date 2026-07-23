package com.morbid.lingtuagent.ai.controller;

import com.morbid.lingtuagent.ai.agent.TravelPlannerAgent;
import com.morbid.lingtuagent.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/planner")
@RequiredArgsConstructor
public class PlannerController {

    private final TravelPlannerAgent travelPlannerAgent;

    @PostMapping("/plan")
    public Result<String> planTrip(@RequestParam String request) {
        String plan = travelPlannerAgent.planTrip(request);
        return Result.success(plan);
    }
}