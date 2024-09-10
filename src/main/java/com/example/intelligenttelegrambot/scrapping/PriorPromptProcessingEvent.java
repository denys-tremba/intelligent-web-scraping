package com.example.intelligenttelegrambot.scrapping;

import org.springframework.context.ApplicationEvent;

public class PriorPromptProcessingEvent extends ApplicationEvent {
    public PriorPromptProcessingEvent(Object source) {
        super(source);
    }
}
