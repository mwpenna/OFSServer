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

public class TypeErrorDigesterTest {

    private TypeErrorDigester objectUnderTest = new TypeErrorDigester();
    private JsonSchema schema;

    @Before
    public void setUp() throws Exception
    {
        SchemaFactory factory = new SchemaFactory();
        schema = factory.getJsonSchema(JsonLoader.fromResource("/errors/type-schema.json"));
    }

    @Test
    public void testGlobalType()
            throws IOException
    {
        JSONArrayBuilder builder = new JSONArrayBuilder()
                .add("Hello").add("Goodbye");
        JsonNode json = JsonLoader.fromReader(new StringReader(builder.build().toString()));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "type");

        assertEquals(1, errors.getGlobalErrors().size());
        OFSError error = errors.getErrors().get(0);
        assertEquals("test.type_mismatch", error.getCode());
        assertEquals(2, error.getProperties().size());
    }

    @Test
    public void testSimplePropertyType()
            throws IOException
    {
        JSONObjectBuilder builder = new JSONObjectBuilder()
                .add("contact", "John");
        JsonNode json = JsonLoader.fromReader(new StringReader(builder.build().toString()));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "type");

        assertEquals(1, errors.getFieldErrors().size());
        OFSError error = errors.getFieldError("contact");
        assertEquals("test.contact.type_mismatch", error.getCode());
    }

    @Test
    public void testLowerCamelPropertyType()
            throws IOException
    {
        JSONObjectBuilder builder = new JSONObjectBuilder()
                .add("ticketAmount", "string");
        JsonNode json = JsonLoader.fromReader(new StringReader(builder.build().toString()));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "type");

        assertEquals(1, errors.getFieldErrors().size());
        OFSError error = errors.getFieldError("ticketAmount");
        assertEquals("test.ticket_amount.type_mismatch", error.getCode());
    }

    @Test
    public void testLowerUnderscorePropertyType()
            throws IOException
    {
        JSONObjectBuilder builder = new JSONObjectBuilder()
                .add("street_name", true);
        JsonNode json = JsonLoader.fromReader(new StringReader(builder.build().toString()));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "type");

        assertEquals(1, errors.getFieldErrors().size());
        OFSError error = errors.getFieldError("street_name");
        assertEquals("test.street_name.type_mismatch", error.getCode());
    }

    @Test
    public void testHyphenatedPropertyType()
            throws IOException
    {
        JSONObjectBuilder builder = new JSONObjectBuilder()
                .add("currency-code", 22);
        JsonNode json = JsonLoader.fromReader(new StringReader(builder.build().toString()));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "type");

        assertEquals(1, errors.getFieldErrors().size());
        OFSError error = errors.getFieldError("currency-code");
        assertEquals("test.currency_code.type_mismatch", error.getCode());
    }

    @Test
    public void testSimpleSubObjectPropertyType()
            throws IOException
    {
        JSONObjectBuilder builder = new JSONObjectBuilder()
                .add("contact", new JSONObjectBuilder()
                        .add("name", 22)
                );
        JsonNode json = JsonLoader.fromReader(new StringReader(builder.build().toString()));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "type");

        assertEquals(1, errors.getFieldErrors().size());
        OFSError error = errors.getFieldError("contact.name");
        assertEquals("test.contact.name.type_mismatch", error.getCode());
    }

    @Test
    public void testLowerCamelSubObjectPropertyType()
            throws IOException
    {
        JSONObjectBuilder builder = new JSONObjectBuilder()
                .add("contact", new JSONObjectBuilder()
                        .add("phoneNumber", 22)
                );
        JsonNode json = JsonLoader.fromReader(new StringReader(builder.build().toString()));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "type");

        assertEquals(1, errors.getFieldErrors().size());
        OFSError error = errors.getFieldError("contact.phoneNumber");
        assertEquals("test.contact.phone_number.type_mismatch", error.getCode());
    }

    @Test
    public void testArrayEntityPropertyType()
            throws IOException
    {
        JSONObjectBuilder builder = new JSONObjectBuilder()
                .add("customerId", new JSONObjectBuilder()
                        .add("href", 22)
                );
        JsonNode json = JsonLoader.fromReader(new StringReader(builder.build().toString()));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "type");

        assertEquals(1, errors.getFieldErrors().size());
        OFSError error = errors.getFieldError("customerId");
        assertEquals("test.customer_id.type_mismatch", error.getCode());
    }

    @Test
    public void testArrayItemPropertyType()
            throws IOException
    {
        JSONObjectBuilder builder = new JSONObjectBuilder()
                .add("customerId", new JSONArrayBuilder()
                        .add("string")
                );
        JsonNode json = JsonLoader.fromReader(new StringReader(builder.build().toString()));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "type");

        assertEquals(1, errors.getFieldErrors().size());
        OFSError error = errors.getFieldError("customerId.items");
        assertEquals("test.customer_id.items.type_mismatch", error.getCode());
    }

    @Test
    public void testArraySubItemPropertyType()
            throws IOException
    {
        JSONObjectBuilder builder = new JSONObjectBuilder()
                .add("customerId", new JSONArrayBuilder()
                        .add(new JSONObjectBuilder()
                                .add("href", 22))
                );
        JsonNode json = JsonLoader.fromReader(new StringReader(builder.build().toString()));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "type");

        assertEquals(1, errors.getFieldErrors().size());
        OFSError error = errors.getFieldError("customerId.items.href");
        assertEquals("test.customer_id.items.href.type_mismatch", error.getCode());
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