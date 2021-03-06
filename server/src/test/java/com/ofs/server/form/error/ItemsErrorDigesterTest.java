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

public class ItemsErrorDigesterTest {
    private JsonSchema schema;

    @Before
    public void setUp() throws Exception
    {
        SchemaFactory factory = new SchemaFactory();
        schema = factory.getJsonSchema(JsonLoader.fromResource("/errors/items-schema.json"));
    }


    @Test
    public void testAdditionalItemsFailsOnWashington() throws Exception
    {
        AdditionalItemsErrorDigester objectUnderTest = new AdditionalItemsErrorDigester();

        JsonNode json = JsonLoader.fromReader(new StringReader("{ \"address\":[ 1600, \"Pennsylvania\", \"Avenue\", \"NW\", \"Washington\"]}"));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "additionalItems");
        assertEquals(1, errors.size());
        OFSError error = errors.getFieldError("address");
        assertEquals("test.address.additional_items_not_allowed", error.getCode());
    }


    @Test
    public void testTooManyItemsFails() throws Exception
    {
        MaxItemsErrorDigester objectUnderTest = new MaxItemsErrorDigester();

        JsonNode json = JsonLoader.fromReader(new StringReader("{\"parents\":[\"John\", \"Debbie\", \"Frankie\"]}"));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "maxItems");
        assertEquals(1, errors.size());
        OFSError error = errors.getFieldError("parents");
        assertEquals("test.parents.array_too_large", error.getCode());
    }

    @Test
    public void testTooFewItemsFails() throws Exception
    {
        MinItemsErrorDigester objectUnderTest = new MinItemsErrorDigester();

        JsonNode json = JsonLoader.fromReader(new StringReader("{\n\"parents\":[\n]\n}"));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "minItems");
        assertEquals(1, errors.size());
        OFSError error = errors.getFieldError("parents");
        assertEquals("test.parents.array_too_small", error.getCode());
    }


    /**
     * In case we want to actually support indexed array definitions. At present I am
     * testing that we collapse them as I don't want to encourage the use of arrays as
     * essentially structures rather than collections of like typed objects.
     */
    @Test
    public void testIndexedItem() throws Exception
    {
        TypeErrorDigester objectUnderTest = new TypeErrorDigester();

        JsonNode json = JsonLoader.fromReader(new StringReader("{ \"address\":[ \"1600\", \"Pennsylvania\", \"Avenue\", \"NW\"]}"));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "type");
        assertEquals(1, errors.size());
        OFSError error = errors.getFieldError("address.items");
        assertEquals("test.address.items.type_mismatch", error.getCode());
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
