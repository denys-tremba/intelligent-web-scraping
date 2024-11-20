package com.example.intelligentwebscrapping.infrastructure.web;

import com.example.intelligentwebscrapping.infrastructure.web.command.AbstractCommand;
import com.example.intelligentwebscrapping.infrastructure.web.command.CommandFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class TelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);
    private final TelegramClient telegramClient;
    private final String token;
    private final CommandFactory commandFactory = new CommandFactory();

    public TelegramBot(@Value("${bot.token}") String token) {
        this.token = token;
        this.telegramClient = new OkHttpTelegramClient(token);
        setCommand();
    }

    private void setCommand() {
        try {
            telegramClient.execute(SetMyCommands.builder()
                    .command(new BotCommand("start_new_conversation", """
                            Start new conversation on fresh topic.
                            Format: '/<commandName>'
                            """))
                    .command(new BotCommand("enter_uri", """
                            Enter uri for further processing.
                            Format: '/<commandName> <uri>'
                            """))
                    .command(new BotCommand("ask_question", """
                            Ask a single message question on entered uri.
                            Format: '/<commandName> <question>'
                            """))
                    .command(new BotCommand("end_conversation", """
                            End current conversation.
                            Format: '/<commandName>'
                            """))
                    .build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String getBotToken() {
        return token;
    }


    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }


    @Override
    public void consume(Update update) {
        logger.info("Before consuming update {}", update);
        AbstractCommand command = commandFactory.get(update, this);
        command.execute();
    }

    public void sendTextMessage(String chatId, String text) {
        try {
            telegramClient.execute(SendMessage.builder().disableWebPagePreview(true).chatId(chatId).text(text).build());
        } catch (TelegramApiException e) {
            throw new TelegramBotProcessingException(e);
        }
    }

}
