package com.example.intelligentwebscrapping.infrastructure.web.command;

import com.example.intelligentwebscrapping.domain.UserId;
import com.example.intelligentwebscrapping.domain.IntelligentWebScrappingSystem;
import com.example.intelligentwebscrapping.infrastructure.web.TelegramBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandFactory {
    private static final Logger logger = LoggerFactory.getLogger(CommandFactory.class);
    private static final Pattern pattern = Pattern.compile("^/(\\w+)( (.*))?$");
    private final IntelligentWebScrappingSystem system = new IntelligentWebScrappingSystem();

    public AbstractCommand get(Update update, TelegramBot telegramBot) {
        String textInputValue = update.getMessage().getText();
        Matcher matcher = pattern.matcher(textInputValue);
        if (matcher.find()) {
            String command = matcher.group(1);
            logger.info("Before executing {} command", command);
            return switch (command) {
                case "start_new_conversation" ->
                        new StartNewConversationCommand(system, telegramBot, new UserId(update.getMessage().getChatId().toString()));
                case "enter_uri" -> new EnterUriCommand(system, URI.create(matcher.group(3)), telegramBot);
                case "end_conversation" -> new EndConversationCommand(system, telegramBot);
                case "ask_question" -> new AskQuestionCommand(system, matcher.group(3), telegramBot);
                default -> new UnknownCommand(system, telegramBot);
            };
        } else {
            return new IncorrectSyntaxCommand(system, textInputValue, telegramBot);
        }
    }
}
