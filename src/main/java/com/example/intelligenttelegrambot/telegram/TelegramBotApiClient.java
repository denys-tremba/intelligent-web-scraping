package com.example.intelligenttelegrambot.telegram;

import com.example.intelligenttelegrambot.scrapping.Scrapper.UriAlreadyScrapedException;
import com.example.intelligenttelegrambot.scrapping.ScrappingAssistant;
import com.example.intelligenttelegrambot.scrapping.RagPipelineService;
import com.example.intelligenttelegrambot.scrapping.Scrapper;
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

import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TelegramBotApiClient implements SpringLongPollingBot, LongPollingUpdateConsumer {
    public static final String SCRAPE = "scrape";
    private static final Pattern pattern = Pattern.compile("^/(scrape) (.*)$");
    private final TelegramClient telegramClient;
    private final String token;
    private final ScrappingAssistant supportAssistant;
    private final RagPipelineService ragPipelineService;
    Scrapper scrapper;
    private final ConcurrentMap <Long, String> hostnameByUserId = new ConcurrentHashMap<>();

    public TelegramBotApiClient(@Value("${bot.token}") String token, ScrappingAssistant supportAssistant, RagPipelineService ragPipelineService) {
        this.token = token;
        this.telegramClient = new OkHttpTelegramClient(token);
        this.supportAssistant = supportAssistant;
        this.ragPipelineService = ragPipelineService;
        this.scrapper = new Scrapper();

    }

    private void doScraping(Update update, URI uri) {
        Long chatId = update.getMessage().getChatId();
        sendMessage(chatId, "Processing " + uri);
        try {
            Path path = scrapper.scrape(uri);
            ragPipelineService.performPipeline(path.toUri().toString(), uri);
            sendMessage(chatId, "Uri has been already scraped. I am ready to answer your questions.");
        } catch (UriAlreadyScrapedException e) {
            sendMessage(chatId, "Uri was previously scraped. I am ready to answer your questions.");
        }
        hostnameByUserId.put(chatId, uri.getHost());
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
    public void consume(List<Update> list) {

        for (Update update : list) {
            Matcher matcher = pattern.matcher(update.getMessage().getText());
            if (matcher.matches()) {
                doScraping(update, URI.create(matcher.group(2)));
            } else {
                SendMessage map = map(update);
                consume(map);
            }

        }
    }

    private void sendMessage(Long chatId, String text) {
        try {
            telegramClient.execute(SendMessage.builder().disableWebPagePreview(true).chatId(chatId).text(text).build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
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
            reply = supportAssistant.chat(chatId.toString(), message.getText(), hostnameByUserId.getOrDefault(chatId, "gaia.cs.umass.edu"));
        } catch (NonTransientAiException e) {
            return SendMessage.builder().disableWebPagePreview(true).chatId(chatId).text("Please try to resend message again in few minutes. I have reached my token limit...").build();
        }
        return SendMessage.builder().disableWebPagePreview(true).chatId(chatId).text(reply).build();
    }
}
