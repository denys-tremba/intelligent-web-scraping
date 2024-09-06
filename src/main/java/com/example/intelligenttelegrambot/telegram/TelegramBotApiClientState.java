package com.example.intelligenttelegrambot.telegram;

import java.net.URI;
import java.nio.file.Path;

public abstract class TelegramBotApiClientState {
    protected final TelegramBotApiClient telegramBotApiClient;

    TelegramBotApiClientState(TelegramBotApiClient client) {
        this.telegramBotApiClient = client;
    }
    abstract Path scrape(URI uri);
    abstract void prompt(String prompt);
}
