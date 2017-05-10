package com.ofs.server.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

@JsonDeserialize
public class ZoneDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {


    @Override
    public ZonedDateTime deserialize(JsonParser parser, DeserializationContext context)
            throws IOException, JsonProcessingException
    {
        return ZonedDateTime.parse(parser.getText(), ISO_OFFSET_DATE_TIME).withZoneSameLocal(ZoneId.of("UTC"));
    }
}
