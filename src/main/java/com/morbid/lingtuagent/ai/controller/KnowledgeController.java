package com.morbid.lingtuagent.ai.controller;

import com.morbid.lingtuagent.ai.model.vo.KnowledgeDocVO;
import com.morbid.lingtuagent.ai.service.KnowledgeService;
import com.morbid.lingtuagent.common.Result;
import com.morbid.lingtuagent.model.entity.User;
import com.morbid.lingtuagent.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;
    private final UserService userService;

    @PostMapping("/upload")
    public Result<KnowledgeDocVO> uploadDoc(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String title) {
        Long userId = getCurrentUserId();
        return Result.success(knowledgeService.uploadDoc(userId, file, title));
    }

    @GetMapping("/list")
    public Result<List<KnowledgeDocVO>> listDocs() {
        Long userId = getCurrentUserId();
        return Result.success(knowledgeService.listDoc(userId));
    }

    @PostMapping("/query")
    public Result<String> queryKnowledge(@RequestParam String question) {
        Long userId = getCurrentUserId();
        return Result.success(knowledgeService.queryKnowledge(userId, question));
    }

    @DeleteMapping("/{docId}")
    public Result<Void> deleteDocument(@PathVariable Long docId) {
        Long userId = getCurrentUserId();
        knowledgeService.deleteDoc(userId, docId);
        return Result.success();
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        User user = userService.findByUsername(username);
        return user.getId();
    }
}