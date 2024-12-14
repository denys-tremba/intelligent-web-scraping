package com.example.intelligentwebscrapping.infrastructure.ai;

import com.example.intelligentwebscrapping.domain.Answer;
import com.example.intelligentwebscrapping.domain.Conversation;
import com.example.intelligentwebscrapping.domain.Question;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ArtificialIntelligenceFacade {
    private final IAiAssistant IAiAssistant;

    public ArtificialIntelligenceFacade(
            @Qualifier("aiAssistant")
//            @Qualifier("translationAiAssistantProxy")
            IAiAssistant IAiAssistant) {
        this.IAiAssistant = IAiAssistant;
    }

    public Answer prompt(Conversation conversation, Question question) {
        return IAiAssistant.chat(conversation, question);
    }
    public Answer promptStreaming(Conversation conversation, Question question) {
        return IAiAssistant.chat(conversation, question);
    }
}
