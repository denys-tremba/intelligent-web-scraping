package com.example.intelligenttelegrambot.customer;

import org.springframework.context.ApplicationEvent;

public class PriorPromptProcessingEvent extends ApplicationEvent {
    public PriorPromptProcessingEvent(Object source) {
        super(source);
    }
}
