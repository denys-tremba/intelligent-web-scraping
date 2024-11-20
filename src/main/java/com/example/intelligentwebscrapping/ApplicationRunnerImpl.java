package com.example.intelligentwebscrapping;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationRunnerImpl implements ApplicationRunner {

    final ChatClient.Builder chatClientBuilder;
    public ApplicationRunnerImpl(ChatClient.Builder chatClientBuilder) {
        this.chatClientBuilder = chatClientBuilder;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println(chatClientBuilder.build().prompt("Tell me a joke").call().content());
    }
}
