package com.example.intelligenttelegrambot.scrapping;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;

import java.io.File;

public class EnhancedSimpleVectorStore extends SimpleVectorStore {
    private static final Logger logger = LoggerFactory.getLogger(EnhancedSimpleVectorStore.class);
    private final File file;

    public EnhancedSimpleVectorStore(EmbeddingModel embeddingModel, File file) {
        super(embeddingModel);
        this.file = file;
    }
    @PostConstruct
    public void postConstruct() {
        if(file.exists()) {
            load(file);
            logger.info("Loaded {} from file {}", this.getClass().getSimpleName(), this.file);
            return;
        }
        logger.info("Skipped loading {} from file {} because it does not exists", this.getClass().getSimpleName(), this.file);
    }

    @PreDestroy
    public void preDestroy() {
        save(file);
        logger.info("Saved {} to file {}", this.getClass().getSimpleName(), this.file);
    }
}
