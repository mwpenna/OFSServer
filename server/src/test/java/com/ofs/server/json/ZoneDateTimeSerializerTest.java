package com.ofs.server.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ZoneDateTimeSerializerTest {

    @Mock
    private JsonGenerator generator;

    @Mock
    private SerializerProvider provider;

    @Test
    public void testDateConversion() throws Exception
    {
        ZoneDateTimeSerializer objectUnderTest = new ZoneDateTimeSerializer();
        ZonedDateTime dateTime = ZonedDateTime.of(2016,5,3,15,25,00,838000000, ZoneId.of("UTC"));
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assertEquals("2016-05-03T15:25:00.838Z",
                        invocation.getArgumentAt(0, String.class));
                return null;
            }
        }).when(generator).writeString(anyString());
        objectUnderTest.serialize(dateTime, generator, provider);
        verify(generator, times(1)).writeString(anyString());
    }
}