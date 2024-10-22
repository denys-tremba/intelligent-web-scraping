package com.example.intelligenttelegrambot.infrastructure.configuration;

import com.example.intelligenttelegrambot.scrapping.EnhancedSimpleVectorStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.transformers.TransformersEmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration(proxyBeanMethods = false)
public class AiConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(AiConfiguration.class);

    @Bean
    TextSplitter textSplitter() {
        return new TokenTextSplitter();
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        return new TransformersEmbeddingModel();
    }
}
