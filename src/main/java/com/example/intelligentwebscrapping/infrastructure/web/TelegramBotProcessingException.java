package com.example.intelligentwebscrapping.infrastructure.web;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramBotProcessingException extends RuntimeException {
    public TelegramBotProcessingException(TelegramApiException e) {
        super(e);
    }
}
