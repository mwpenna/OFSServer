package com.ofs.server.form.error;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.ofs.server.form.schema.JsonSchema;
import com.ofs.server.form.schema.SchemaFactory;
import com.ofs.server.model.OFSErrors;
import com.ofs.server.model.OFSError;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;

import static junit.framework.TestCase.assertEquals;

public class LengthErrorDigesterTest {

    private JsonSchema schema;

    @Before
    public void setUp() throws Exception
    {
        SchemaFactory factory = new SchemaFactory();
        schema = factory.getJsonSchema(JsonLoader.fromResource("/errors/length-schema.json"));
    }

    @Test
    public void testMaxLengthFails() throws Exception
    {
        MaxLengthErrorDigester objectUnderTest = new MaxLengthErrorDigester();

        JsonNode json = JsonLoader.fromReader(new StringReader("{\"sir-name\": \"Alexadropovia\"}"));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "maxLength");
        assertEquals(1, errors.size());
        OFSError error = errors.getFieldError("sir-name");
        assertEquals("test.sir_name.maximum_length_exceeded", error.getCode());
    }


    @Test
    public void testMinLengthFails() throws Exception
    {
        MinLengthErrorDigester objectUnderTest = new MinLengthErrorDigester();

        JsonNode json = JsonLoader.fromReader(new StringReader("{\"given-name\":\"Ed\"}"));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "minLength");
        assertEquals(1, errors.size());
        OFSError error = errors.getFieldError("given-name");
        assertEquals("test.given_name.minimum_length_missed", error.getCode());
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
