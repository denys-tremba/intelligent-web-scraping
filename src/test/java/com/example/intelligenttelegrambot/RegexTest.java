package com.example.intelligenttelegrambot;

import org.jsoup.Jsoup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    void name() throws IOException {
        URI uri = URI.create("https://gaia.cs.umass.edu/kurose_ross/about.php");
        String s = Jsoup.connect(uri.toString())
                .get()
                .wholeText();
        System.out.println(s.lines()
                .filter(string -> !string.isBlank())
                .map(String::strip)
                .collect(Collectors.joining(System.lineSeparator())));
    }
}
