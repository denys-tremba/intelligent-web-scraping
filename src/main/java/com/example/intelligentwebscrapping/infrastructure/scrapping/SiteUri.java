package com.example.intelligentwebscrapping.infrastructure.scrapping;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class SiteUri {
    @JacksonXmlProperty(localName = "loc")
    public
    String location;
}
