package com.example.intelligenttelegrambot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class IntelligentTelegramBotApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplicationBuilder(IntelligentTelegramBotApplication.class)
                .application();
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);
    }

}
