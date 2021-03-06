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

public class FormatErrorDigesterTest {

    private FormatErrorDigester objectUnderTest = new FormatErrorDigester();
    private JsonSchema schema;

    @Before
    public void setUp() throws Exception
    {
        SchemaFactory factory = new SchemaFactory();
        schema = factory.getJsonSchema(JsonLoader.fromResource("/errors/format-schema.json"));
    }


    @Test
    public void testUriFormatFails() throws Exception
    {
        JsonNode json = JsonLoader.fromReader(new StringReader( "{\"href\":\"+23:not-an-href\"}"));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "format");
        assertEquals(1, errors.size());
        OFSError error = errors.getFieldError("href");
        assertEquals("test.href.invalid_format", error.getCode());

        // NOTE: It would appear uri validation is VERY lose. Probably should validate our uri
        // using a pattern which ensures (http|https)://(host)(:port)/(path)
    }

    @Test
    public void testEmailFormatFails() throws Exception
    {
        JsonNode json = JsonLoader.fromReader(new StringReader("{\"email\":\"not-an-email\"}"));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "format");
        assertEquals(1, errors.size());
        OFSError error = errors.getFieldError("email");
        assertEquals("test.email.invalid_format", error.getCode());
    }

    @Test
    public void testDateFormatFails() throws Exception
    {
        JsonNode json = JsonLoader.fromReader(new StringReader("{\"currentDate\":\"not-a-date\"}"));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "format");
        assertEquals(1, errors.size());
        OFSError error = errors.getFieldError("currentDate");
        assertEquals("test.current_date.invalid_format", error.getCode());
    }


    @Test
    public void testUUIDFormatFails() throws Exception
    {
        JsonNode json = JsonLoader.fromReader(new StringReader("{\"id\":\"not-an-uuid\"}"));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "format");
        assertEquals(1, errors.size());
        OFSError error = errors.getFieldError("id");
        assertEquals("test.id.invalid_format", error.getCode());
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