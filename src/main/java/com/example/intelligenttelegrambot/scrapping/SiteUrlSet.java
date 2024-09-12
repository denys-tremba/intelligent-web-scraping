package com.example.intelligenttelegrambot.scrapping;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.Set;

@JacksonXmlRootElement(namespace = "urlset")
public class SiteUrlSet {
    @JacksonXmlElementWrapper(useWrapping = false)
            @JacksonXmlProperty(localName = "url")
    public
    Set<SiteUrl> urlSet;
}
