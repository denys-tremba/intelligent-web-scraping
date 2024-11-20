package com.example.intelligentwebscrapping.infrastructure.scrapping;

import com.example.intelligentwebscrapping.infrastructure.ai.EtlPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.file.Path;
import java.util.Map;

@Component
public class UriScrappingFacade {
    private final Scrapper scrapper;
    private final EtlPipeline etlPipeline;
    private static final Logger logger = LoggerFactory.getLogger( UriScrappingFacade.class );


    public UriScrappingFacade(Scrapper scrapper, EtlPipeline etlPipeline) {
        this.scrapper = scrapper;
        this.etlPipeline = etlPipeline;
    }

    public void scrape(URI uri)  {
        if (uri.getPath().endsWith("sitemap.xml")) {
            logger.info("Before scrapping whole site {}" , uri);
            scrapeWebsite(uri);
        } else {
            logger.info("Before scrapping single page {}" , uri);
            scrapeSinglePage(uri);
        }
    }

    private void scrapeWebsite(URI uri) {
        Map<URI, Path> paths = scrapper.scrapeWebSite(uri);
        for (Map.Entry<URI, Path> entry : paths.entrySet()) {
            etlPipeline.performPipeline(entry.getValue().toUri().toString(), entry.getKey());
        }
    }

    private void scrapeSinglePage(URI uri) {
        Path path = scrapper.scrapeSinglePage(uri);
        etlPipeline.performPipeline(path.toUri().toString(), uri);
    }
}
