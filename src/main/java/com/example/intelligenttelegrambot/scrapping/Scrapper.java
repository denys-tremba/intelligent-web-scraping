package com.example.intelligenttelegrambot.scrapping;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class Scrapper {

    public Path scrape(URI uri) throws UriAlreadyScrapedException {
        Path path = Path.of(uri.getHost() + uri.getPath() + ".txt");
        if (Files.exists(path)) {
            throw new UriAlreadyScrapedException(uri);
        }
        try {
            String text = Jsoup.connect(uri.toString())
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .wholeText()
                    .lines()
                    .filter(string -> !string.isBlank())
                    .map(String::strip)
                    .collect(Collectors.joining(System.lineSeparator()));
            Files.createDirectories(path.getParent());
            Files.write(path, text.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return path;
    }

    public static class UriAlreadyScrapedException extends Exception {
        public UriAlreadyScrapedException(URI uri) {
            super(uri.toString());
        }
    }
}
