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
import xpertss.json.JSONArrayBuilder;
import xpertss.json.JSONObjectBuilder;

import java.io.StringReader;

import static junit.framework.TestCase.assertEquals;

public class PropertiesErrorDigesterTest {

    private JsonSchema schema;

    @Before
    public void setUp() throws Exception
    {
        SchemaFactory factory = new SchemaFactory();
        schema = factory.getJsonSchema(JsonLoader.fromResource("/errors/properties-schema.json"));
    }

    @Test
    public void testAdditionalPropertiesNotAllowed() throws Exception
    {
        AdditionalPropertiesErrorDigester objectUnderTest = new AdditionalPropertiesErrorDigester();

        JSONObjectBuilder builder = new JSONObjectBuilder()
                .add("contact", new JSONObjectBuilder()
                        .add("age", 22)
                );
        JsonNode json = JsonLoader.fromReader(new StringReader(builder.build().toString()));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "additionalProperties");
        assertEquals(1, errors.size());
        OFSError error = errors.getErrors().get(0);
        assertEquals("test.contact.additional_properties_not_allowed", error.getCode());
    }

    @Test
    public void testAdditionalPropertiesNotAllowedArrayItem() throws Exception
    {
        AdditionalPropertiesErrorDigester objectUnderTest = new AdditionalPropertiesErrorDigester();

        JSONObjectBuilder builder = new JSONObjectBuilder()
                .add("customerId", new JSONArrayBuilder()
                        .add(new JSONObjectBuilder()
                                .add("href", "string").add("name", "string")
                        )
                );
        JsonNode json = JsonLoader.fromReader(new StringReader(builder.build().toString()));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "additionalProperties");
        assertEquals(1, errors.size());
        OFSError error = errors.getErrors().get(0);
        assertEquals("test.customer_id.items.additional_properties_not_allowed", error.getCode());
    }

    @Test
    public void testTooManyProperties() throws Exception
    {
        MaxPropertiesErrorDigester objectUnderTest = new MaxPropertiesErrorDigester();

        JSONObjectBuilder builder = new JSONObjectBuilder()
                .add("contact", new JSONObjectBuilder()
                        .add("name", "John").add("email", "joeblow@manheim.com")
                );
        JsonNode json = JsonLoader.fromReader(new StringReader(builder.build().toString()));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "maxProperties");
        assertEquals(1, errors.size());
        OFSError error = errors.getErrors().get(0);
        assertEquals("test.contact.too_many_properties_found", error.getCode());
    }

    @Test
    public void testTooFewProperties() throws Exception
    {
        MinPropertiesErrorDigester objectUnderTest = new MinPropertiesErrorDigester();

        JSONObjectBuilder builder = new JSONObjectBuilder()
                .add("address", new JSONObjectBuilder()
                        .add("state", "GA").add("zip", "30180")
                );
        JsonNode json = JsonLoader.fromReader(new StringReader(builder.build().toString()));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "minProperties");
        assertEquals(1, errors.size());
        OFSError error = errors.getErrors().get(0);
        assertEquals("test.address.too_few_properties_found", error.getCode());
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
