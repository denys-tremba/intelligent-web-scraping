package com.example.intelligentwebscrapping.infrastructure.web.command;

import com.example.intelligentwebscrapping.domain.IntelligentWebScrappingSystem;
import com.example.intelligentwebscrapping.infrastructure.web.TelegramBot;

public class EndConversationCommand extends AbstractCommand {
    public EndConversationCommand(IntelligentWebScrappingSystem system, TelegramBot telegramBot) {
        super(system, telegramBot);
    }

    @Override
    public void execute() {
        system.endConversation();
        telegramBot.sendTextMessage(system.getConversationId().value(), "Conversation has been ended");
    }

}
