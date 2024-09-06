package com.example.intelligenttelegrambot.customer;

import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EtlPipelineService {
    private static final String WEBSITE_CONTEXT_ROOT_KEY = "website_context_root_key";
    private final VectorStore vectorStore;
    private final DocumentTransformer documentTransformer;

    public EtlPipelineService(VectorStore vectorStore, DocumentTransformer documentTransformer) {
        this.vectorStore = vectorStore;
        this.documentTransformer = documentTransformer;
    }


    public void performPipeline(Resource resource) {
        try {
            performPipeline(resource.getURI().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void performPipeline(String url) {
        TextReader textReader = new TextReader(url);
        textReader.getCustomMetadata().put(WEBSITE_CONTEXT_ROOT_KEY, url);
        vectorStore.write(
                documentTransformer.transform(
                        textReader.read()));
    }
}
