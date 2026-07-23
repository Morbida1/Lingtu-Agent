package com.morbid.lingtuagent.ai.config;

import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;// Redis 向量存储（待 Redis Stack 就绪后切换）
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2q.AllMiniLmL6V2QuantizedEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;// 内存存储（当前使用）
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RagConfig {
    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private Integer redisPort;

    @Bean
    public EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2QuantizedEmbeddingModel();
    }

    @Bean
    public EmbeddingStore knowledgeEmbeddingStore() {
        // ========== 内存存储（当前使用） ==========
        return new InMemoryEmbeddingStore();

        // ========== Redis 向量存储（待 Redis Stack 就绪后切换） ==========
        // return RedisEmbeddingStore.builder()
        //         .host(redisHost)
        //         .port(redisPort)
        //         .indexName("lingtu_knowledge")
        //         .dimension(384)
        //         .build();
    }
}