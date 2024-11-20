package com.example.intelligentwebscrapping.infrastructure.web.command;

import com.example.intelligentwebscrapping.domain.IntelligentWebScrappingSystem;
import com.example.intelligentwebscrapping.infrastructure.web.TelegramBot;

public class AskQuestionCommand extends AbstractCommand {
    private final String question;

    public AskQuestionCommand(IntelligentWebScrappingSystem system, String question, TelegramBot telegramBot) {
        super(system, telegramBot);
        this.question = question;
    }

    @Override
    public void execute() {
        system.askQuestion(question);
        telegramBot.sendTextMessage(system.getConversationId().value(), system.getLastAnswer().value());
    }

}
