package com.ofs.server.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ofs.server.config.JacksonConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class OFSErrorsTest {

    private static ObjectMapper ofsObjectMapper;

    @BeforeClass
    public static void init()
    {
        JacksonConfiguration config = new JacksonConfiguration();
        ofsObjectMapper = config.objectMapper();
    }

    @Test
    public void testErrorGettersReturnCorrectErrors()
    {
        OFSErrors objectUnderTest = new OFSErrors();
        objectUnderTest.rejectValue("global.error_one", "First global error");
        objectUnderTest.rejectValue("field.error_one", "field1", "First field error");
        objectUnderTest.rejectValue("global.error_two", "Second global error");
        objectUnderTest.rejectValue("field.error_two", "field2", "Second field error");
        assertEquals(4, objectUnderTest.getErrors().size());
        assertEquals(2, objectUnderTest.getGlobalErrors().size());
        for(OFSError error : objectUnderTest.getGlobalErrors()) {
            assertTrue(error.getCode().startsWith("global."));
        }
        assertEquals(2, objectUnderTest.getFieldErrors().size());
        for(OFSError error : objectUnderTest.getFieldErrors()) {
            assertTrue(error.getCode().startsWith("field."));
        }
    }

    @Test
    public void testFieldErrors()
    {
        OFSErrors objectUnderTest = new OFSErrors();
        objectUnderTest.rejectValue("field.error_one", "field1", "First field error");
        objectUnderTest.rejectValue("field.error_two", "field2", "Second field error");
        assertEquals("field.error_one", objectUnderTest.getFieldError("field1").getCode());
        assertEquals("field.error_two", objectUnderTest.getFieldError("field2").getCode());
    }

    @Test
    public void testGetFirstFieldError()
    {
        OFSErrors objectUnderTest = new OFSErrors();
        objectUnderTest.rejectValue("field.error_one", "field1", "First field error");
        objectUnderTest.rejectValue("field.error_two", "field1", "Second field error");
        assertEquals("field.error_one", objectUnderTest.getFieldError("field1").getCode());
        assertEquals("field.error_one", objectUnderTest.getFieldError("field1").getCode());
    }

    @Test
    public void testDropsDuplicateErrors()
    {
        OFSErrors objectUnderTest = new OFSErrors();
        objectUnderTest.rejectValue("field.error_one", "field1", "First field error");
        objectUnderTest.rejectValue("field.error_one", "field1", "Second field error");
        assertEquals(1, objectUnderTest.size());
        assertEquals("First field error", objectUnderTest.getFieldError("field1").getDeveloperMessage());
    }

    @Test
    public void testGetFieldErrorWithNullFieldNameDoesntReturnGlobals()
    {
        OFSErrors objectUnderTest = new OFSErrors();
        objectUnderTest.rejectValue("global.error_one", "First global error");
        objectUnderTest.rejectValue("field.error_one", "field1", "First field error");
        assertNull(objectUnderTest.getFieldError(null));
        assertEquals(0, objectUnderTest.getFieldErrors(null).size());
    }
}