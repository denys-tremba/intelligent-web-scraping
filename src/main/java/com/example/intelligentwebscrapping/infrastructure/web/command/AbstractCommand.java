package com.example.intelligentwebscrapping.infrastructure.web.command;

import com.example.intelligentwebscrapping.domain.IntelligentWebScrappingSystem;
import com.example.intelligentwebscrapping.infrastructure.web.TelegramBot;

public abstract class AbstractCommand {
    protected IntelligentWebScrappingSystem system;
    protected TelegramBot telegramBot;

    public AbstractCommand(IntelligentWebScrappingSystem system, TelegramBot telegramBot) {
        this.system = system;
        this.telegramBot = telegramBot;
    }

    public abstract void execute();

}
