package com.ofs.server.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.time.ZonedDateTime;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

@JsonSerialize
public class ZoneDateTimeSerializer extends JsonSerializer<ZonedDateTime> {

    @Override
    public void serialize(ZonedDateTime date, JsonGenerator json, SerializerProvider provider)
            throws IOException, JsonProcessingException
    {
        json.writeString(ISO_OFFSET_DATE_TIME.format(date));
    }
}
