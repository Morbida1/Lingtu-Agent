package com.morbid.lingtuagent.ai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.morbid.lingtuagent.ai.model.vo.KnowledgeDocVO;
import com.morbid.lingtuagent.ai.service.KnowledgeService;
import com.morbid.lingtuagent.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/knowledge")
@RequiredArgsConstructor
public class AdminKnowledgeController {

    private final KnowledgeService knowledgeService;

    @GetMapping("/page")
    public Result<IPage<KnowledgeDocVO>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(knowledgeService.pageAllDocs(pageNum, pageSize, keyword));
    }

    @GetMapping("/{id}")
    public Result<KnowledgeDocVO> get(@PathVariable Long id) {
        return Result.success(knowledgeService.getDocById(id));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        knowledgeService.deleteDocById(id);
        return Result.success();
    }
}