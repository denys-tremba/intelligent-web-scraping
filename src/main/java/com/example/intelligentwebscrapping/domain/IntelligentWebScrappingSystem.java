package com.example.intelligentwebscrapping.domain;

import java.net.URI;
import java.util.UUID;

public class IntelligentWebScrappingSystem {
    private Conversation conversation = new Conversation(new UserId(UUID.randomUUID().toString()));
    public void startConversation(UserId userId) {
        conversation = new Conversation(userId);
    }
    public void enterUri(URI uri) {
        conversation.enterUri(uri);
    }

    public void askQuestion(String question) {
        conversation.askQuestion(question);
    }

    public Answer getLastAnswer() {
        return conversation.getLastAnswer();
    }

    public void endConversation() {
        conversation.becomeComplete();
    }

    public UserId getConversationId() {
        return conversation.getConversationId();
    }
}
