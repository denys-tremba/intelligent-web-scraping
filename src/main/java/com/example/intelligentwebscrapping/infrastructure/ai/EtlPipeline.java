package com.example.intelligentwebscrapping.infrastructure.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class EtlPipeline {
    private static final Logger logger = LoggerFactory.getLogger(EtlPipeline.class);
    public static final String WEBSITE_CONTEXT_ROOT_KEY = "hostname";
    public static final String SOURCE_OF_TRUTH_KEY = "source_of_truth";
    private final VectorStore vectorStore;
    private final TextSplitter textSplitter;

    public EtlPipeline(VectorStore vectorStore, TextSplitter textSplitter) {
        this.vectorStore = vectorStore;
        this.textSplitter = textSplitter;
    }


    public void performPipeline(String fileUri, URI uri) {
//        if(true) return;
//        MarkdownDocumentReaderConfig.builder()
//                .build();
//        DocumentReader textReader = new MarkdownDocumentReader(fileUri);
        TextReader textReader = new TextReader(fileUri);
        textReader.setCharset(UTF_8);
        textReader.getCustomMetadata().put(WEBSITE_CONTEXT_ROOT_KEY, uri.getHost());
        textReader.getCustomMetadata().put(SOURCE_OF_TRUTH_KEY, uri.getPath());
        logger.info("Before rag pipeline {}", fileUri);
        List<Document> documents = textReader.get();
        documents = textSplitter.apply(documents);
        vectorStore.add(documents);
        logger.info("After rag pipeline {}", fileUri);
    }
}
