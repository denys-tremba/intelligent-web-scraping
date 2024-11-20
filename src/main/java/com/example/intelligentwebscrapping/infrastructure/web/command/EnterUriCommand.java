package com.example.intelligentwebscrapping.infrastructure.web.command;

import com.example.intelligentwebscrapping.domain.IntelligentWebScrappingSystem;
import com.example.intelligentwebscrapping.infrastructure.web.TelegramBot;

import java.net.URI;

public class EnterUriCommand extends AbstractCommand {
    private final URI uri;

    public EnterUriCommand(IntelligentWebScrappingSystem system, URI uri, TelegramBot telegramBot) {
        super(system, telegramBot);
        this.uri = uri;
    }

    @Override
    public void execute() {
        system.enterUri(uri);
        telegramBot.sendTextMessage(system.getConversationId().value(), "Uri %s has been entered successfully and it is being processed".formatted(uri));
    }

}
