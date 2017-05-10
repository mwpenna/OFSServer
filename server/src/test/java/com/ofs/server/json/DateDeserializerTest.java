package com.ofs.server.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ofs.server.config.JacksonConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;
import xpertss.time.Chronology;

import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class DateDeserializerTest {

    private static final Chronology chrono = Chronology.create(TimeZone.getTimeZone("UTC"));

    private static ObjectMapper jackson;

    @BeforeClass
    public static void init()
    {
        JacksonConfiguration config = new JacksonConfiguration();
        jackson = config.objectMapper();
    }


    @Test
    public void testIsoDateWithMillisParsesToJavaUtilDate() throws Exception
    {
        ObjectNode raw = jackson.createObjectNode();
        raw.put("date", "2016-10-22T12:35:11.123Z");
        ObjectReader reader = jackson.readerFor(Bill.class);
        Bill decoded = reader.readValue(raw);
        assertEquals(chrono.newDate(2016,10,22,12,35,11,123), decoded.getDate());
    }

    @Test
    public void testIsoDateWithoutMillisParsesToJavaUtilDate() throws Exception
    {
        ObjectNode raw = jackson.createObjectNode();
        raw.put("date", "2016-10-22T12:35:11Z");
        ObjectReader reader = jackson.readerFor(Bill.class);
        Bill decoded = reader.readValue(raw);
        assertEquals(chrono.newDate(2016,10,22,12,35,11), decoded.getDate());
    }
}