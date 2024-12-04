package com.example.intelligentwebscrapping;

import com.deepl.api.DeepLException;
import com.deepl.api.GlossaryLanguagePair;
import com.deepl.api.TextResult;
import com.deepl.api.Translator;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
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

    @Test
    void name5() throws DeepLException, InterruptedException {
        String authKey = "36b47dfc-802f-4d03-a3e8-b0e164bb1749:fx";  // Replace with your key
        Translator translator = new Translator(authKey);
        TextResult result =
                translator.translateText("Привіт світе!", Locale.forLanguageTag("Uk-ua").getLanguage(), "en-GB");
        System.out.println(result.getBilledCharacters());
        System.out.println(result.getText()); // "Bonjour, le monde !"
    }

    @Test
    void name6() {
        System.out.println(Locale.getDefault().getLanguage());
        System.out.println(Locale.getDefault().getCountry());
        System.out.println(Locale.getDefault().getDisplayName());
        System.out.println(Locale.getDefault().getDisplayCountry());
        System.out.println(Locale.getDefault().getISO3Country());
        System.out.println(Locale.forLanguageTag("Uk-ua"));
    }

    @Test
    void name7() throws DeepLException, InterruptedException {
        String authKey = "36b47dfc-802f-4d03-a3e8-b0e164bb1749:fx";  // Replace with your key
        Translator translator = new Translator(authKey);
        List<GlossaryLanguagePair> glossaryLanguages =
                translator.getGlossaryLanguages();
        for (GlossaryLanguagePair glossaryLanguage : glossaryLanguages) {
            System.out.printf("%s to %s\n",
                    glossaryLanguage.getSourceLanguage(),
                    glossaryLanguage.getTargetLanguage());
            // Example: "en to de", "de to en", etc.
        }
    }

    @Test
    void test8() {
        System.out.println(Path.of("").toAbsolutePath());
        System.out.println(Path.of(".").toAbsolutePath());
        System.out.println(Path.of("./").toAbsolutePath());
    }
}
