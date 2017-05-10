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
import xpertss.json.JSONArrayBuilder;
import xpertss.json.JSONObjectBuilder;

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
        JSONObjectBuilder builder = new JSONObjectBuilder()
                .add("address", new JSONObjectBuilder()
                        .add("street", "1046 Brown St").add("zip", "30180")
                );
        JsonNode json = JsonLoader.fromReader(new StringReader(builder.build().toString()));

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
        JSONObjectBuilder builder = new JSONObjectBuilder()
                .add("contact", new JSONObjectBuilder()
                        .add("name", "Joe").add("gender", "M")
                );
        JsonNode json = JsonLoader.fromReader(new StringReader(builder.build().toString()));

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
        JSONObjectBuilder builder = new JSONObjectBuilder()
                .add("contact", new JSONObjectBuilder()
                        .add("name", "Joe").add("email", "joe@manheim.com")
                ).add("address", new JSONObjectBuilder()
                        .add("city", "Atlanta").add("state", "GA")
                );
        JsonNode json = JsonLoader.fromReader(new StringReader(builder.build().toString()));

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
        JSONObjectBuilder builder = new JSONObjectBuilder()
                .add("contact", new JSONObjectBuilder()
                        .add("name", "Joe").add("email", "joe@manheim.com")
                ).add("customerId", new JSONArrayBuilder()
                        .add(new JSONObjectBuilder()
                                .add("href", "http://first/")
                        ).add(new JSONObjectBuilder())
                );
        JsonNode json = JsonLoader.fromReader(new StringReader(builder.build().toString()));

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