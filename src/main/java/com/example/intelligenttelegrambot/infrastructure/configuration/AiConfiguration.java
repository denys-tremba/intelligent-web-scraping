package com.example.intelligenttelegrambot.infrastructure.configuration;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration(proxyBeanMethods = false)
public class AiConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(AiConfiguration.class);

    @Bean
    CommandLineRunner ingestTermOfServiceToVectorStore(
            EmbeddingModel embeddingModel, VectorStore vectorStore) {

        return args -> {
            // Ingest the document into the vector store
            String text = Jsoup.connect("https://gaia.cs.umass.edu/kurose_ross/index.php")
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .text();
            Path path = Path.of("content.txt");
            Files.write(path, text.getBytes());
            vectorStore.write(
                    new TokenTextSplitter().transform(
                            new TextReader(path.toUri().toString()).read()));

//            vectorStore.similaritySearch("Cancelling Bookings").forEach(doc -> {
//                logger.info("Similar Document: {}", doc.getContent());
//            });
        };
    }

//    @Bean
//    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
//        return new SimpleVectorStore(embeddingModel);
//    }

    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }
}
