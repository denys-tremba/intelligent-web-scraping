package com.example.intelligentwebscrapping.infrastructure.web.command;

import com.example.intelligentwebscrapping.domain.UserId;
import com.example.intelligentwebscrapping.domain.IntelligentWebScrappingSystem;
import com.example.intelligentwebscrapping.infrastructure.web.TelegramBot;

public class StartNewConversationCommand extends AbstractCommand {

    private final UserId userId;

    public StartNewConversationCommand(IntelligentWebScrappingSystem system, TelegramBot telegramBot, UserId userId) {
        super(system, telegramBot);
        this.userId = userId;
    }

    @Override
    public void execute() {
        system.startConversation(userId);
        telegramBot.sendTextMessage(userId.value(), "Conversation with id %s has been started".formatted(userId.value()));

    }

}
