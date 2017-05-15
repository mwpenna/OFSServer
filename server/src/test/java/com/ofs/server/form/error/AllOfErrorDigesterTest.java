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

public class AllOfErrorDigesterTest {

    private AllOfErrorDigester objectUnderTest = new AllOfErrorDigester();
    private JsonSchema schema;

    @Before
    public void setUp() throws Exception
    {
        SchemaFactory factory = new SchemaFactory();
        schema = factory.getJsonSchema(JsonLoader.fromResource("/errors/allof-schema.json"));
    }

    @Test
    public void testAllOfStringFails() throws Exception
    {
        JsonNode json = JsonLoader.fromReader(new StringReader("{\n\"name\":\"Erin \"\n}"));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "allOf");
        assertEquals(1, errors.size());

        OFSError error = errors.getFieldError("name");
        assertEquals("test.name.subschema_all_of_mismatch", error.getCode());
    }

    @Test
    public void testAllOfTypeFails() throws Exception
    {

        JsonNode json = JsonLoader.fromReader(new StringReader("{\n\"values\":[\n10,\n\"John\", \nnull]}"));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "allOf");
        assertEquals(1, errors.size());  // apparently, it doesn't recurse through array on failure

        OFSError errorOne = errors.getFieldError("values.items");
        assertEquals("test.values.items.subschema_all_of_mismatch", errorOne.getCode());
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