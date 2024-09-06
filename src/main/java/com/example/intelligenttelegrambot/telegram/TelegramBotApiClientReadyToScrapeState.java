package com.example.intelligenttelegrambot.telegram;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

public class TelegramBotApiClientReadyToScrapeState extends TelegramBotApiClientState {
    TelegramBotApiClientReadyToScrapeState(TelegramBotApiClient client) {
        super(client);
    }

    @Override
    public Path scrape(URI uri) {
        String text = null;
        try {
            text = Jsoup.connect(uri.toString())
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .text();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Path path = Path.of(uri.getHost());
        try {
            Files.write(path, text.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return path;
    }

    @Override
    public void prompt(String prompt) {
    }
}
