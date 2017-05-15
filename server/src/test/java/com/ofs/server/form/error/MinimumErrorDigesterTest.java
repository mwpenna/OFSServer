package com.ofs.server.form.error;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.ofs.server.form.schema.JsonSchema;
import com.ofs.server.form.schema.SchemaFactory;
import com.ofs.server.model.OFSError;
import com.ofs.server.model.OFSErrors;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;

import static junit.framework.TestCase.assertEquals;

public class MinimumErrorDigesterTest {

    MinimumErrorDigester objectUnderTest = new MinimumErrorDigester();
    private JsonSchema schema;

    @Before
    public void setUp() throws Exception
    {
        SchemaFactory factory = new SchemaFactory();
        schema = factory.getJsonSchema(JsonLoader.fromResource("/errors/minimum-schema.json"));
    }

    @Test
    public void testMinimumServiceFails() throws Exception
    {
        JsonNode json = JsonLoader.fromReader(new StringReader("{\"service\":3}"));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "minimum");
        assertEquals(1, errors.size());
        OFSError error = errors.getFieldError("service");
        assertEquals("test.service.minimum_too_small", error.getCode());
    }


    @Test
    public void testMinimumAgeFails() throws Exception
    {
        JsonNode json = JsonLoader.fromReader(new StringReader("{\"age\":17}"));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "minimum");
        assertEquals(1, errors.size());
        OFSError error = errors.getFieldError("age");
        assertEquals("test.age.minimum_too_small", error.getCode());
    }

    @Test
    public void testMinimumExclusiveFails() throws Exception
    {
        JsonNode json = JsonLoader.fromReader(new StringReader("{\"age\":18}"));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "minimum");
        assertEquals(1, errors.size());
        OFSError error = errors.getFieldError("age");
        assertEquals("test.age.minimum_not_exclusive", error.getCode());
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