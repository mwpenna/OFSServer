package com.ofs.server.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

@JsonDeserialize
public class DoubleDeserializer extends JsonDeserializer<Double> {

    public DoubleDeserializer() { }

    @Override
    public Double deserialize(JsonParser parser, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        String doubleStr = parser.getText();

        if (doubleStr.isEmpty() || doubleStr == null) {
            return null;
        }

        return new Double(doubleStr);
    }
}

