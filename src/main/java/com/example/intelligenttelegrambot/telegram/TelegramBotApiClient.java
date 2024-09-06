package com.example.intelligenttelegrambot.telegram;

import com.example.intelligenttelegrambot.customer.CustomerSupportAssistant;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
public class TelegramBotApiClient implements SpringLongPollingBot {
    private final TelegramClient telegramClient;

    private final String token;
    private final CustomerSupportAssistant supportAssistant;

    public TelegramBotApiClient(@Value("${bot.token}") String token, CustomerSupportAssistant supportAssistant) {
        this.token = token;
        this.telegramClient = new OkHttpTelegramClient(token);
        this.supportAssistant = supportAssistant;
    }

    @Override
    public String getBotToken() {
        return token;
    }


    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return new LongPollingUpdateConsumer() {
            @Override
            public void consume(List<Update> list) {
                list.stream().map(this::map).forEachOrdered(this::consume);
            }

            private void consume(SendMessage sendMessage) {
                try {
                    telegramClient.execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }

            private SendMessage map(Update update) {
                Message message = update.getMessage();
                Long chatId = message.getChatId();
                String reply = null;
                try {
                    reply = supportAssistant.chat(chatId.toString(), message.getText());
                } catch (NonTransientAiException e) {
                    return SendMessage.builder().chatId(chatId).text("Please try to send message again...").build();
                }
                return SendMessage.builder().chatId(chatId).text(reply).build();
            }
        };
    }
}
