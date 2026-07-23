package com.morbid.lingtuagent.ai.service;

import com.morbid.lingtuagent.ai.model.vo.KnowledgeDocVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface KnowledgeService {
    KnowledgeDocVO uploadDoc(Long userId, MultipartFile file, String title);
    List<KnowledgeDocVO> listDoc(Long userId);
    String queryKnowledge(Long userId, String question);
    void deleteDoc(Long userId, Long docId);
}
