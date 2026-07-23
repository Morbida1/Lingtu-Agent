package com.morbid.lingtuagent.ai.controller;

import com.morbid.lingtuagent.ai.model.entity.PromptTemplate;
import com.morbid.lingtuagent.ai.service.PromptService;
import com.morbid.lingtuagent.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prompt")
@RequiredArgsConstructor
public class PromptController {

    private final PromptService promptService;

    @GetMapping("/list")
    public Result<List<PromptTemplate>> listAll() {
        return Result.success(promptService.listAll());
    }

    @GetMapping("/{category}")
    public Result<PromptTemplate> getByCategory(@PathVariable String category) {
        return Result.success(promptService.getByCategory(category));
    }

    @PostMapping
    public Result<PromptTemplate> create(@RequestBody PromptTemplate template) {
        return Result.success(promptService.create(template));
    }

    @PutMapping
    public Result<PromptTemplate> update(@RequestBody PromptTemplate template) {
        return Result.success(promptService.update(template));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        promptService.delete(id);
        return Result.success();
    }

    @PostMapping("/render")
    public Result<String> render(@RequestParam String category, @RequestBody Map<String, String> variables) {
        return Result.success(promptService.renderPrompt(category, variables));
    }
}