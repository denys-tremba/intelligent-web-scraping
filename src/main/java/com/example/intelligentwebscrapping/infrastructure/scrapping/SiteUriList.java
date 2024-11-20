package com.example.intelligentwebscrapping.infrastructure.scrapping;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(namespace = "urlset")
public class SiteUriList {
    @JacksonXmlElementWrapper(useWrapping = false)
            @JacksonXmlProperty(localName = "url")
    public List<SiteUri> urlSet;
}
