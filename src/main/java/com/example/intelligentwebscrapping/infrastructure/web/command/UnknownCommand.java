package com.example.intelligentwebscrapping.infrastructure.web.command;

import com.example.intelligentwebscrapping.domain.IntelligentWebScrappingSystem;
import com.example.intelligentwebscrapping.infrastructure.web.TelegramBot;

public class UnknownCommand extends AbstractCommand {
    public UnknownCommand(IntelligentWebScrappingSystem system, TelegramBot telegramBot) {
        super(system, telegramBot);
    }

    @Override
    public void execute() {
        telegramBot.sendTextMessage(system.getConversationId().value(), "Unknown command has been provided");
    }

}
