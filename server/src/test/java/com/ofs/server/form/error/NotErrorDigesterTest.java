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
import xpertss.json.JSONObjectBuilder;

import java.io.StringReader;

import static junit.framework.TestCase.assertEquals;

public class NotErrorDigesterTest {

    private NotErrorDigester objectUnderTest = new NotErrorDigester();
    private JsonSchema schema;

    @Before
    public void setUp() throws Exception
    {
        SchemaFactory factory = new SchemaFactory();
        schema = factory.getJsonSchema(JsonLoader.fromResource("/errors/not-schema.json"));
    }

    @Test
    public void testNotHrefFails() throws Exception
    {
        JSONObjectBuilder builder = new JSONObjectBuilder()
                .add("href", "https://api.manheim.com/tickets");
        JsonNode json = JsonLoader.fromReader(new StringReader(builder.build().toString()));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "not");
        assertEquals(1, errors.size());
        OFSError error = errors.getFieldError("href");
        assertEquals("test.href.not_acceptable", error.getCode());

    }

    @Test
    public void testNotUpdatedOnCreatedOnFails() throws Exception
    {

        JSONObjectBuilder builder = new JSONObjectBuilder()
                .add("href", "https://api.manheim.com/tickets")
                .add("updatedOn", "2015-12-05T06:33:51.012Z");
        JsonNode json = JsonLoader.fromReader(new StringReader(builder.build().toString()));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "not");
        assertEquals(2, errors.size());

        OFSError hef = errors.getFieldError("href");
        assertEquals("test.href.not_acceptable", hef.getCode());

        OFSError updatedOn = errors.getFieldError("updatedOn");
        assertEquals("test.updated_on.not_acceptable", updatedOn.getCode());
    }

    @Test
    public void testNotHrefUpdatedOnCreatedOnFails() throws Exception
    {
        JSONObjectBuilder builder = new JSONObjectBuilder()
                .add("href", "https://api.manheim.com/tickets")
                .add("createdOn", "2015-12-05T06:33:51.012Z")
                .add("updatedOn", "2015-12-05T06:33:51.012Z");
        JsonNode json = JsonLoader.fromReader(new StringReader(builder.build().toString()));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "not");
        assertEquals(3, errors.size());

        OFSError hef = errors.getFieldError("href");
        assertEquals("test.href.not_acceptable", hef.getCode());

        OFSError updatedOn = errors.getFieldError("updatedOn");
        assertEquals("test.updated_on.not_acceptable", updatedOn.getCode());

        OFSError createdOn = errors.getFieldError("createdOn");
        assertEquals("test.created_on.not_acceptable", createdOn.getCode());
    }

    @Test
    public void testNotCurrencyFails() throws Exception
    {
        JSONObjectBuilder builder = new JSONObjectBuilder()
                .add("currency", "EUR");
        JsonNode json = JsonLoader.fromReader(new StringReader(builder.build().toString()));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "not");
        assertEquals(1, errors.size());
        OFSError error = errors.getFieldError("currency");
        assertEquals("test.currency.not_acceptable", error.getCode());

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