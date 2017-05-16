package com.ofs.server.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ofs.server.config.JacksonConfiguration;
import org.junit.BeforeClass;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class DateSerializerTest {

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
        objectUnderTest.setDate(newDate(2016,10,22,12,35,11,123));
        ObjectNode encoded = jackson.valueToTree(objectUnderTest);
        JsonNode node = encoded.findValue("date");
        assertEquals("2016-10-22T12:35:11.123Z", node.asText());
    }

    private Date newDate(int year, int month, int day, int hour, int min, int sec) {
        return newDate(year, month, day, hour, min, sec, 0);
    }

    private Date newDate(int year, int month, int day, int hour, int min, int sec, int millis) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.getDefault());
        cal.set(year, month - 1, day, hour, min, sec);
        cal.set(14, millis);
        return cal.getTime();
    }
}