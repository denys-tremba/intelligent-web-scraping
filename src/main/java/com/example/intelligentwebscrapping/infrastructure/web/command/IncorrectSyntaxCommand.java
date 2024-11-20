package com.example.intelligentwebscrapping.infrastructure.web.command;

import com.example.intelligentwebscrapping.domain.IntelligentWebScrappingSystem;
import com.example.intelligentwebscrapping.infrastructure.web.TelegramBot;

public class IncorrectSyntaxCommand extends AbstractCommand {
    private final String textInputValue;

    public IncorrectSyntaxCommand(IntelligentWebScrappingSystem system, String textInputValue, TelegramBot telegramBot) {
        super(system, telegramBot);
        this.textInputValue = textInputValue;
    }

    @Override
    public void execute() {
        telegramBot.sendTextMessage(system.getConversationId().value(), "Incorrect message format. Please refer to /help command");
    }
}
