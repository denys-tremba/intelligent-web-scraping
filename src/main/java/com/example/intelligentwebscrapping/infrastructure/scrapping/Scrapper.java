package com.example.intelligentwebscrapping.infrastructure.scrapping;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Scrapper {
    private static final Path base = Path.of("scrapes");
    private XmlMapper xmlMapper = ((XmlMapper) new XmlMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false));

    public Path scrapeSinglePage(URI uri) {
        Path path = base.resolve(Path.of(uri.getHost() + uri.getPath() + ".md"));
        if (Files.exists(path)) {
//            throw new UriAlreadyScrapedException(uri);
            return path;
        }
        try {
            String html = Jsoup.connect(uri.toString()).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").referrer("http://www.google.com").get().html();
            Files.createDirectories(path.getParent());
            String md = FlexmarkHtmlConverter.builder().build().convert(html);
            Files.write(path, md.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return path;
    }

    public Map<URI, Path> scrapeWebSite(URI sitemap) {
        Map<URI, Path> paths = new HashMap<>();
        SiteUriList siteUriList;
        try (InputStream inputStream = sitemap.toURL().openStream()) {
            siteUriList = xmlMapper.readValue(inputStream, SiteUriList.class);
            siteUriList.urlSet = siteUriList.urlSet.subList(0, 6);
            for (SiteUri siteUri : siteUriList.urlSet) {
                URI uri = URI.create(siteUri.location);
                Path path = scrapeSinglePage(uri);
                paths.put(uri, path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return paths;
    }
}