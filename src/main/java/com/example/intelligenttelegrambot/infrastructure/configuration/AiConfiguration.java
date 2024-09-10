package com.example.intelligenttelegrambot.infrastructure.configuration;

import com.example.intelligenttelegrambot.scrapping.RagPipelineService;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration(proxyBeanMethods = false)
public class AiConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(AiConfiguration.class);

    @Bean
    DocumentTransformer documentTransformer() {
        return new TokenTextSplitter(8192 ,400, 5,1000, false);
    }

    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }
}
