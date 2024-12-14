package com.example.intelligentwebscrapping.domain;

import java.net.URI;
import java.util.UUID;

public class IntelligentWebScrappingSystem {
    private Conversation conversation = new Conversation();
    public void startConversation() {
        conversation = new Conversation();
    }
    public void enterUri(URI uri) {
        conversation.enterUri(uri);
    }

    public void askQuestion(String question) {
        conversation.askQuestion(question);
    }
    public void askQuestionStreaming(String question) {
        conversation.askQuestionStreaming(question);
    }
    public Answer getLastAnswer() {
        return conversation.getLastAnswer();
    }

    public void endConversation() {
        conversation.becomeComplete();
    }



    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }
}
