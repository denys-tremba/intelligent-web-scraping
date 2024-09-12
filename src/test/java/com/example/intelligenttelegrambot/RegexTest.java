package com.example.intelligenttelegrambot;

import com.example.intelligenttelegrambot.infrastructure.configuration.AiConfiguration;
import com.example.intelligenttelegrambot.scrapping.RagPipelineService;
import com.example.intelligenttelegrambot.scrapping.Scrapper;
import com.example.intelligenttelegrambot.scrapping.SiteUrl;
import com.example.intelligenttelegrambot.scrapping.SiteUrlSet;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SpringBootTest()
public class RegexTest {
    @Autowired RagPipelineService rag;
    @Test
    void testScrapeCommandRegex() {
        Pattern pattern = Pattern.compile("^/(scrape) (.*)$");
        Matcher matcher = pattern.matcher("/scrape https://gist.github.com/skeller88/5eb73dc0090d4ff1249a");
        Assertions.assertTrue(matcher.matches());
        Assertions.assertEquals("scrape", matcher.group(1));
        Assertions.assertEquals("https://gist.github.com/skeller88/5eb73dc0090d4ff1249a", matcher.group(2));
    }


}
