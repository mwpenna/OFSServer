package com.ofs.server.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ofs.server.config.JacksonConfiguration;
import org.junit.BeforeClass;
import xpertss.time.Chronology;

import java.util.TimeZone;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class DateSerializerTest {

    private static final Chronology chrono = Chronology.create(TimeZone.getTimeZone("UTC"));
    private static ObjectMapper jackson;

    @BeforeClass
    public static void init()
    {
        JacksonConfiguration config = new JacksonConfiguration();
        jackson = config.objectMapper();
    }

    public void testJavaUtilDateFormatsToIsoWithMillisAndZ() throws Exception
    {
        Bill objectUnderTest = new Bill(UUID.randomUUID());
        objectUnderTest.setDate(chrono.newDate(2016,10,22,12,35,11,123));
        ObjectNode encoded = jackson.valueToTree(objectUnderTest);
        JsonNode node = encoded.findValue("date");
        assertEquals("2016-10-22T12:35:11.123Z", node.asText());
    }
}