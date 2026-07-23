package com.morbid.lingtuagent.ai.service;

import com.morbid.lingtuagent.ai.model.entity.PromptTemplate;
import java.util.List;
import java.util.Map;

public interface PromptService {
    PromptTemplate getByCategory(String category);
    List<PromptTemplate> listAll();
    PromptTemplate create(PromptTemplate template);
    PromptTemplate update(PromptTemplate template);
    void delete(Long id);
    String renderPrompt(String category, Map<String, String> variables);
}