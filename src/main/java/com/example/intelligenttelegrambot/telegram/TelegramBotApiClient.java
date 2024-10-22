package com.example.intelligenttelegrambot.telegram;

import com.example.intelligenttelegrambot.scrapping.*;
import com.example.intelligenttelegrambot.scrapping.Scrapper.UriAlreadyScrapedException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.LinkPreviewOptions;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TelegramBotApiClient implements SpringLongPollingBot, LongPollingUpdateConsumer {
    private static final Logger logger = LoggerFactory.getLogger(TelegramBotApiClient.class);
    private static final Pattern pattern = Pattern.compile("^/(\\w+)( (.*))?$");
    private final TelegramClient telegramClient;
    private final String token;
    private final ScrappingAssistant supportAssistant;
    private final RagPipelineService ragPipelineService;
    Scrapper scrapper;
    ObjectMapper mapper = new XmlMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public TelegramBotApiClient(@Value("${bot.token}") String token, ScrappingAssistant supportAssistant, RagPipelineService ragPipelineService) {
        this.token = token;
        this.telegramClient = new OkHttpTelegramClient(token);
        this.supportAssistant = supportAssistant;
        this.ragPipelineService = ragPipelineService;
        this.scrapper = new Scrapper();

    }

    private void scrape(Update update, URI baseUri) {
        Long chatId = update.getMessage().getChatId();
        sendMessage(chatId, "Processing " + baseUri + ". This can take up to few minutes. Please wait...");
        // sitemap
        if (baseUri.toString().endsWith("sitemap.xml")) {
            try {
                SiteUrlSet siteUrlSet;
                try (InputStream inputStream = baseUri.toURL().openStream()) {
                    siteUrlSet = mapper.readValue(inputStream, SiteUrlSet.class);
                }
                siteUrlSet.urlSet = new HashSet<>(new ArrayList<>(siteUrlSet.urlSet).subList(0, 6));
                siteUrlSet.urlSet.forEach(url->{
                    doScraping(baseUri, chatId, url.location);
                });
                sendMessage(chatId, "Site has been already scraped. I am ready to answer your questions.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // single page
        else {
            doScraping(baseUri, update.getMessage().getChatId(), baseUri.toString());
            supportAssistant.eraseConversation(update.getMessage().getChatId().toString());
            sendMessage(chatId, "Site has been already scraped. I am ready to answer your questions.");
        }

    }

    private void doScraping(URI baseUri, Long chatId, String location) {
        try {
            logger.info("Before processing {}", location);
            Path path = scrapper.scrape(URI.create(location));
            ragPipelineService.performPipeline(path.toUri().toString(), baseUri, location);
        } catch (UriAlreadyScrapedException e) {
            sendMessage(chatId, "Uri %s has been already scraped".formatted(location));
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
    public void consume(List<Update> list) {
        list = List.of(list.get(list.size() - 1));
        for (Update update : list) {
            logger.info("Before consuming update {}", update);
            Matcher matcher = pattern.matcher(update.getMessage().getText());
            if (matcher.matches()) {
                String command = matcher.group(1);
                logger.info("Before executing {} command", command);
                if (command.equalsIgnoreCase("scrape")) {
                    try {
                        scrape(update, new URL(matcher.group(3)).toURI());
                    } catch (URISyntaxException | MalformedURLException e) {
                        sendMessage(update.getMessage().getChatId(), "Invalid url");
                    }
                } else if (command.equalsIgnoreCase("observability")) {
                    sendMessage(update.getMessage().getChatId(), "[inline URL](http://localhost:3000/d/edxvmkn39u670c/spring-ai?orgId=1&refresh=5s)");
                } else {
                    sendMessage(update.getMessage().getChatId(), "Unknown command");
                }

            } else {
                SendMessage map = map(update);
                consume(map);
            }

        }
    }

    private void sendMessage(Long chatId, String text) {
        try {
            telegramClient.execute(SendMessage.builder()
//                    .disableWebPagePreview(true)
                    .chatId(chatId)
                    .text(text)
                    .build());
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
        String reply;
        try {
            reply = supportAssistant.chat(chatId.toString(), message.getText());
        } catch (NonTransientAiException e) {
            return SendMessage.builder().disableWebPagePreview(true).chatId(chatId).text("Please try to resend message again in few minutes. I have reached my token limit...").build();
        }
        return SendMessage.builder().disableWebPagePreview(true).chatId(chatId).text(reply).build();
    }
}
