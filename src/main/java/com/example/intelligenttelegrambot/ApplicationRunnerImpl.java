package com.example.intelligenttelegrambot;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//@Component
public class ApplicationRunnerImpl implements ApplicationRunner {
    final EmbeddingModel embeddingModel;
    final VectorStore vectorStore;
    final TextSplitter textSplitter;

    public ApplicationRunnerImpl(EmbeddingModel embeddingModel, VectorStore vectorStore, TextSplitter textSplitter) {
        this.embeddingModel = embeddingModel;
        this.vectorStore = vectorStore;
        this.textSplitter = textSplitter;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var textReader = new TextReader(Path.of("C:\\Users\\user\\SelfStudying\\Java\\intelligent-web-scrapping\\scrapes\\docs.spring.io\\spring-ai\\reference\\concepts.html.md").toUri().toString());
        List<Document> docs = textSplitter.split(textReader.get());
        EmbeddingResponse embeddingResponse = embeddingModel.embedForResponse(docs.stream().map(Document::getContent).collect(Collectors.toList()));
        System.out.println(embeddingResponse);
//        vectorStore.add(docs);
    }
}
