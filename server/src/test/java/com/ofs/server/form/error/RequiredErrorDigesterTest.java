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

import java.io.IOException;
import java.io.StringReader;

import static junit.framework.TestCase.assertEquals;

public class RequiredErrorDigesterTest {

    private RequiredErrorDigester objectUnderTest = new RequiredErrorDigester();
    private JsonSchema schema;

    @Before
    public void setUp() throws Exception
    {
        SchemaFactory factory = new SchemaFactory();
        schema = factory.getJsonSchema(JsonLoader.fromResource("/errors/required-schema.json"));
    }

    @Test
    public void testContactRequired()
            throws IOException
    {
        JsonNode json = JsonLoader.fromReader(new StringReader("{\"address\":{\"street\":\"1046 Brown St\", \"zip\":\"30180\"}}"));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "required");
        assertEquals(1, errors.size());
        OFSError error = errors.getFieldError("contact");
        assertEquals("test.contact.required_field_missing", error.getCode());
    }

    @Test
    public void testContactRequiresBothProperties()
            throws IOException
    {
        JsonNode json = JsonLoader.fromReader(new StringReader("{\"contact\":{\"name\":\"Joe\", \"gender\":\"M\"}}"));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "required");
        assertEquals(1, errors.size());
        OFSError error = errors.getFieldError("contact.email");
        assertEquals("test.contact.email.required_field_missing", error.getCode());
    }

    @Test
    public void testAddressPropertiesRequired()
            throws IOException
    {
        JsonNode json = JsonLoader.fromReader(new StringReader("{\"contact\":{\"name\":\"Joe\", \"email\":\"joe@place.com\"},\"address\":{\"city\":\"Atlanta\", \"state\":\"GA\"}}"));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "required");
        assertEquals(2, errors.size());

        OFSError errorOne = errors.getFieldError("address.street");
        assertEquals("test.address.street.required_field_missing", errorOne.getCode());
        OFSError errorTwo = errors.getFieldError("address.zip");
        assertEquals("test.address.zip.required_field_missing", errorTwo.getCode());
    }

    @Test
    public void testArrayHrefRequired()
            throws IOException
    {
        JsonNode json = JsonLoader.fromReader(new StringReader("{\"contact\":{\"name\":\"Joe\", \"email\":\"joe@place.com\"},\"customerId\":[{\"href\":\"http://first/\"},{}]}"));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "required");
        assertEquals(1, errors.size());

        OFSError errorOne = errors.getFieldError("customerId.items.href");
        assertEquals("test.customer_id.items.href.required_field_missing", errorOne.getCode());
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