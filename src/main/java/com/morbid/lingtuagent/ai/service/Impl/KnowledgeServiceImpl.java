package com.morbid.lingtuagent.ai.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.morbid.lingtuagent.ai.mapper.KnowledgeDocMapper;
import com.morbid.lingtuagent.ai.model.entity.KnowledgeDoc;
import com.morbid.lingtuagent.ai.model.vo.KnowledgeDocVO;
import com.morbid.lingtuagent.ai.service.KnowledgeService;
import com.morbid.lingtuagent.ai.util.DocParser;
import com.morbid.lingtuagent.ai.util.TextChunker;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeServiceImpl extends ServiceImpl<KnowledgeDocMapper, KnowledgeDoc> implements KnowledgeService {

    private final DocParser docParser;
    private final TextChunker textChunker;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final ChatModel chatModel;

    @Value("${rag.upload-dir:./uploads/knowledge}")
    private String uploadDir;

    @Override
    public KnowledgeDocVO uploadDoc(Long userId, MultipartFile file, String title) {
        KnowledgeDoc doc = new KnowledgeDoc();
        doc.setUserId(userId);
        doc.setTitle(title != null ? title : file.getOriginalFilename());
        doc.setFileType(getFileExtension(file.getOriginalFilename()));
        doc.setStatus(0);

        try {
            String content = docParser.parse(file);
            doc.setContent(content);

            String filePath = saveFile(file);
            doc.setFilePath(filePath);

            List<String> chunks = textChunker.chunk(content);
            doc.setChunkCount(chunks.size());

            for (String chunk : chunks) {
                TextSegment segment = TextSegment.from(chunk);
                Embedding embedding = embeddingModel.embed(segment).content();
                embeddingStore.add(embedding, segment);
            }

            doc.setStatus(1);
            this.save(doc);
            log.info("文档上传成功: {}, 分块数: {}", doc.getTitle(), chunks.size());

        } catch (Exception e) {
            doc.setStatus(2);
            doc.setErrorMsg(e.getMessage());
            if (doc.getFilePath() == null) {
                doc.setFilePath("");  // 只要不为 null，MyBatis-Plus 就会加入 SQL
            }
            this.save(doc);
            log.error("文档上传失败: {}", e.getMessage(), e);
        }

        KnowledgeDocVO vo = new KnowledgeDocVO();
        BeanUtils.copyProperties(doc, vo);
        return vo;
    }

    @Override
    public List<KnowledgeDocVO> listDoc(Long userId) {
        LambdaQueryWrapper<KnowledgeDoc> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeDoc::getUserId, userId)
                .orderByDesc(KnowledgeDoc::getCreateTime);

        return this.list(wrapper).stream().map(doc -> {
            KnowledgeDocVO vo = new KnowledgeDocVO();
            BeanUtils.copyProperties(doc, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public String queryKnowledge(Long userId, String question) {
        Embedding questionEmbedding = embeddingModel.embed(question).content();

        List<EmbeddingMatch<TextSegment>> matches = embeddingStore.search(
                EmbeddingSearchRequest.builder()
                        .queryEmbedding(questionEmbedding)
                        .maxResults(3)
                        .minScore(0.0)
                        .build()
        ).matches();

        if (matches.isEmpty()) {
            return "知识库中没有找到相关信息。";
        }

        StringBuilder context = new StringBuilder();
        for (int i = 0; i < matches.size(); i++) {
            context.append("相关文档").append(i + 1).append("：\n");
            context.append(matches.get(i).embedded().text()).append("\n\n");
        }

        String prompt = """
            根据以下知识库内容回答问题：
            
            %s
            
            问题：%s
            
            请基于上述信息回答问题，如果知识库中没有相关信息，请说明。
            """.formatted(context.toString(), question);

        return chatModel.chat(prompt);
    }

    @Override
    public void deleteDoc(Long userId, Long docId) {
        this.removeById(docId);
    }

    private String saveFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);
        file.transferTo(filePath.toFile());

        return filePath.toString();
    }

    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int lastDot = filename.lastIndexOf(".");
        return lastDot > 0 ? filename.substring(lastDot + 1).toLowerCase() : "";
    }
}