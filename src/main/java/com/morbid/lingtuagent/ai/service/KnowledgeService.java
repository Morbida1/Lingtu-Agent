package com.morbid.lingtuagent.ai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.morbid.lingtuagent.ai.model.vo.KnowledgeDocVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface KnowledgeService {
    KnowledgeDocVO uploadDoc(Long userId, MultipartFile file, String title);
    List<KnowledgeDocVO> listDoc(Long userId);
    KnowledgeDocVO getDoc(Long userId, Long docId);
    String queryKnowledge(Long userId, String question);
    void deleteDoc(Long userId, Long docId);

    IPage<KnowledgeDocVO> pageAllDocs(int pageNum, int pageSize, String keyword);
    KnowledgeDocVO getDocById(Long docId);
    void deleteDocById(Long docId);
}