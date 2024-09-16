package com.example.intelligenttelegrambot;

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.net.http.HttpResponse.BodyHandlers.ofString;

public class RegexTest {
    @Test
    void testScrapeCommandRegex() {
        Pattern pattern = Pattern.compile("^/(scrape) (.*)$");
        Matcher matcher = pattern.matcher("/scrape https://gist.github.com/skeller88/5eb73dc0090d4ff1249a");
        Assertions.assertTrue(matcher.matches());
        Assertions.assertEquals("scrape", matcher.group(1));
        Assertions.assertEquals("https://gist.github.com/skeller88/5eb73dc0090d4ff1249a", matcher.group(2));
    }

    @Test
    void name() throws IOException, InterruptedException {
        URI uri = URI.create("https://commonmark.org/");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(uri).build();
        String body = httpClient.send(request, ofString()).body();

        String md = FlexmarkHtmlConverter.builder().build().convert(body);

    }

    @Test
    void name1() {
        TextReader reader = new TextReader(Path.of("C:\\Users\\user\\SelfStudying\\Java\\intelligent-web-scrapping\\scrapes\\docs.spring.io\\spring-ai\\reference\\concepts.html.md").toUri().toString());
        List<Document> documents = reader.get();
        System.out.println(documents.size());
        TokenTextSplitter textSplitter = new TokenTextSplitter();
        List<Document> split = textSplitter.split(documents);
        System.out.println(split.size());

    }
}
