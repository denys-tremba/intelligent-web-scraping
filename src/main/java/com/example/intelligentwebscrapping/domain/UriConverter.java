package com.example.intelligentwebscrapping.domain;

import com.ctc.wstx.shaded.msv_core.util.Uri;
import jakarta.persistence.AttributeConverter;
import org.springframework.stereotype.Component;

import java.net.URI;
@Component
public class UriConverter implements AttributeConverter<URI, String> {
    @Override
    public String convertToDatabaseColumn(URI attribute) {
        return attribute.toString();
    }

    @Override
    public URI convertToEntityAttribute(String dbData) {
        return URI.create(dbData);
    }
}
