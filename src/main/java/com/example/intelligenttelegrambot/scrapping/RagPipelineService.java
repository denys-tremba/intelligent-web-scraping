package com.example.intelligenttelegrambot.scrapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class RagPipelineService {
    private static final Logger logger = LoggerFactory.getLogger(RagPipelineService.class);
    public static final String WEBSITE_CONTEXT_ROOT_KEY = "hostname";
    public static final String SOURCE_OF_TRUTH_KEY = "source_of_truth";
    private final VectorStore vectorStore;
    private final DocumentTransformer documentTransformer;

    public RagPipelineService(VectorStore vectorStore, DocumentTransformer documentTransformer) {
        this.vectorStore = vectorStore;
        this.documentTransformer = documentTransformer;
    }


    public void performPipeline(Resource resource, URI uri) {
        try {
            performPipeline(resource.getURI().toString(), uri);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void performPipeline(String fileUri, URI uri) {
        TextReader textReader = new TextReader(fileUri);
        textReader.setCharset(UTF_8);
        textReader.getCustomMetadata().put(WEBSITE_CONTEXT_ROOT_KEY, uri.getHost());
        textReader.getCustomMetadata().put(SOURCE_OF_TRUTH_KEY, uri.toString());
        logger.info("Before rag pipeline");
        vectorStore.write(
                documentTransformer.transform(
                        textReader.read()));
        logger.info("After rag pipeline");
    }
}
