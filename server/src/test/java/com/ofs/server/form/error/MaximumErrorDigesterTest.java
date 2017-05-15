package com.ofs.server.form.error;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.ofs.server.model.OFSErrors;
import com.ofs.server.form.schema.JsonSchema;
import com.ofs.server.form.schema.SchemaFactory;
import com.ofs.server.model.OFSError;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;

import static junit.framework.TestCase.assertEquals;

public class MaximumErrorDigesterTest {

    MaximumErrorDigester objectUnderTest = new MaximumErrorDigester();
    private JsonSchema schema;

    @Before
    public void setUp() throws Exception
    {
        SchemaFactory factory = new SchemaFactory();
        schema = factory.getJsonSchema(JsonLoader.fromResource("/errors/maximum-schema.json"));
    }

    @Test
    public void testMaximumServiceFails() throws Exception
    {
        JsonNode json = JsonLoader.fromReader(new StringReader("{\"service\":21}"));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "maximum");
        assertEquals(1, errors.size());
        OFSError error = errors.getFieldError("service");
        assertEquals("test.service.maximum_too_large", error.getCode());
    }


    @Test
    public void testMinimumAgeFails() throws Exception
    {
        JsonNode json = JsonLoader.fromReader(new StringReader("{\"age\":66}"));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "maximum");
        assertEquals(1, errors.size());
        OFSError error = errors.getFieldError("age");
        assertEquals("test.age.maximum_too_large", error.getCode());
    }

    @Test
    public void testMinimumExclusiveFails() throws Exception
    {
        JsonNode json = JsonLoader.fromReader(new StringReader("{\"age\":65}"));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "maximum");
        assertEquals(1, errors.size());
        OFSError error = errors.getFieldError("age");
        assertEquals("test.age.maximum_not_exclusive", error.getCode());
    }



    private OFSErrors processErrors(ProcessingReport report, ErrorDigester digester, String expectedKeyword)
    {
        OFSErrors errors = new OFSErrors();
        for(ProcessingMessage msg : report) {
            JsonNode error = msg.asJson();
            String keyword = error.findValue("keyword").asText();
            assertEquals(expectedKeyword, keyword);
            digester.digest(errors, "test", error);
        }
        return errors;
    }
}