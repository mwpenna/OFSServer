package com.ofs.server.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ofs.server.config.JacksonConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class DateDeserializerTest {

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
        raw.put("date", "2016-10-22T12:35:11.111Z");
        ObjectReader reader = jackson.readerFor(Bill.class);
        Bill decoded = reader.readValue(raw);
        assertEquals(newDate(2016,10,22,12,35,11).toString(), decoded.getDate().toString());
    }

    @Test
    public void testIsoDateWithoutMillisParsesToJavaUtilDate() throws Exception
    {
        ObjectNode raw = jackson.createObjectNode();
        raw.put("date", "2016-10-22T12:35:11Z");
        ObjectReader reader = jackson.readerFor(Bill.class);
        Bill decoded = reader.readValue(raw);
        assertEquals(newDate(2016,10,22,12,35,11), decoded.getDate());
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