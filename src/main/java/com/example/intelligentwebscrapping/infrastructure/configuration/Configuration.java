package com.example.intelligentwebscrapping.infrastructure.configuration;

import com.example.intelligentwebscrapping.infrastructure.scrapping.Scrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration(proxyBeanMethods = false)
public class Configuration {
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    @Bean
    TextSplitter textSplitter() {
        return new TokenTextSplitter(1000, 350, 5, 10000, true);
//        return new TokenTextSplitter(800, 350, 5, 10000, true);
    }



//    @Bean
//    public ObjectMapper xmlMapper() {
//        return new XmlMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//    }

    @Bean
    public Scrapper scrapper() {
        return new Scrapper();
    }

    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }

}
