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

import java.io.StringReader;

import static junit.framework.TestCase.assertEquals;

public class OneOfErrorDigesterTest {

    private OneOfErrorDigester objectUnderTest = new OneOfErrorDigester();
    private JsonSchema schema;

    @Before
    public void setUp() throws Exception
    {
        SchemaFactory factory = new SchemaFactory();
        schema = factory.getJsonSchema(JsonLoader.fromResource("/errors/oneof-schema.json"));
    }

    @Test
    public void testOneOfStringFails() throws Exception
    {
        JSONObjectBuilder builder = new JSONObjectBuilder()
                .add("name", "Frank");
        JsonNode json = JsonLoader.fromReader(new StringReader(builder.build().toString()));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "oneOf");
        assertEquals(1, errors.size());

        OFSError error = errors.getErrors().get(0);
        assertEquals("test.name.subschema_one_of_mismatch", error.getCode());
    }

    @Test
    public void testAllOfTypeFails() throws Exception
    {
        JSONObjectBuilder builder = new JSONObjectBuilder()
                .add("values", new JSONArrayBuilder()
                        .add(15)
                );
        JsonNode json = JsonLoader.fromReader(new StringReader(builder.build().toString()));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "oneOf");
        assertEquals(1, errors.size());  // apparently, it doesn't recurse through array on failure

        OFSError errorOne = errors.getErrors().get(0);
        assertEquals("test.values.items.subschema_one_of_mismatch", errorOne.getCode());
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