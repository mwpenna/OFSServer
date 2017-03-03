package com.ofs.server.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@JsonSerialize
public class DateSerializer  extends StdSerializer<Date> {

    protected final static String DATE_FORMAT_STR_ISO8601_Z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";


    public DateSerializer()
    {
        super(Date.class);
    }

    @Override
    public void serialize(Date date, JsonGenerator json, SerializerProvider provider)
            throws IOException
    {
        DateFormat df = new SimpleDateFormat(DATE_FORMAT_STR_ISO8601_Z);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        json.writeString(df.format(date));
    }
}
