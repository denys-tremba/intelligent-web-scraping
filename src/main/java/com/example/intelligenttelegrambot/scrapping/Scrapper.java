package com.example.intelligenttelegrambot.scrapping;

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class Scrapper {
    private static final Path base = Path.of("scrapes");

    public Path scrape(URI uri) throws UriAlreadyScrapedException {
        Path path = base.resolve(Path.of(uri.getHost() + uri.getPath() + ".md"));
        if (Files.exists(path)) {
//            throw new UriAlreadyScrapedException(uri);
            return path;
        }
        try {
            String html = Jsoup.connect(uri.toString())
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .html();
            Files.createDirectories(path.getParent());
            String md = FlexmarkHtmlConverter.builder().build().convert(html);
            Files.write(path, md.getBytes());
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
