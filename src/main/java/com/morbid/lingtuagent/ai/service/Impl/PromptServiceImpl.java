package com.morbid.lingtuagent.ai.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.morbid.lingtuagent.ai.mapper.PromptTemplateMapper;
import com.morbid.lingtuagent.ai.model.entity.PromptTemplate;
import com.morbid.lingtuagent.ai.service.PromptService;
import com.morbid.lingtuagent.ai.util.PromptRenderer;
import com.morbid.lingtuagent.common.ResultCode;
import com.morbid.lingtuagent.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PromptServiceImpl extends ServiceImpl<PromptTemplateMapper, PromptTemplate> implements PromptService {

    private final PromptRenderer promptRenderer;

    @Override
    public PromptTemplate getByCategory(String category) {
        LambdaQueryWrapper<PromptTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PromptTemplate::getCategory, category)
                .eq(PromptTemplate::getIsActive, true)
                .last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    public List<PromptTemplate> listAll() {
        return this.list();
    }

    @Override
    public PromptTemplate create(PromptTemplate template) {
        template.setCreatedAt(LocalDateTime.now());
        template.setUpdatedAt(LocalDateTime.now());
        this.save(template);
        return template;
    }

    @Override
    public PromptTemplate update(PromptTemplate template) {
        template.setUpdatedAt(LocalDateTime.now());
        this.updateById(template);
        return template;
    }

    @Override
    public void delete(Long id) {
        this.removeById(id);
    }

    @Override
    public String renderPrompt(String category, Map<String, String> variables) {
        PromptTemplate template = getByCategory(category);
        if (template == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "未找到分类为 " + category + " 的 Prompt 模板");
        }
        return promptRenderer.render(template.getTemplate(), variables);
    }
}