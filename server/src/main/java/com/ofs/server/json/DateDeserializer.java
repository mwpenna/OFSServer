package com.ofs.server.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;


import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@JsonDeserialize
public class DateDeserializer extends StdDeserializer<Date> {

    public DateDeserializer()
    {
        super(Date.class);
    }

    @Override
    public Date deserialize(JsonParser parser, DeserializationContext context)
            throws IOException, JsonProcessingException
    {

        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            return df.parse(parser.getText());
        } catch (ParseException e) {
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                return df.parse(parser.getText());
            } catch(ParseException p) {
                return null;
            }
        }
    }
}
