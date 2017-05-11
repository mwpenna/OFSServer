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
import xpertss.json.JSONObjectBuilder;

import java.io.StringReader;

import static junit.framework.TestCase.assertEquals;

public class DependenciesErrorDigesterTest {
    private DependenciesErrorDigester objectUnderTest = new DependenciesErrorDigester();
    private JsonSchema schema;

    @Before
    public void setUp() throws Exception
    {
        SchemaFactory factory = new SchemaFactory();
        schema = factory.getJsonSchema(JsonLoader.fromResource("/errors/dependencies-schema.json"));
    }

    @Test
    public void testPropertyDependenciesNoBillingInfo() throws Exception
    {
        JSONObjectBuilder builder = new JSONObjectBuilder()
                .add("purchase", new JSONObjectBuilder()
                        .add("credit_card", 12345676555443L)
                );
        JsonNode json = JsonLoader.fromReader(new StringReader(builder.build().toString()));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "dependencies");
        assertEquals(2, errors.size());
        OFSError errorOne = errors.getFieldError("purchase.billing_address");
        assertEquals("test.purchase.billing_address.required_dependency_missing", errorOne.getCode());
        OFSError errorTwo = errors.getFieldError("purchase.billing_zip");
        assertEquals("test.purchase.billing_zip.required_dependency_missing", errorTwo.getCode());
    }

    @Test
    public void testPropertyDependenciesNoBillingZip() throws Exception
    {
        JSONObjectBuilder builder = new JSONObjectBuilder()
                .add("purchase", new JSONObjectBuilder()
                        .add("billing_address", "1046 Brown St")
                );
        JsonNode json = JsonLoader.fromReader(new StringReader(builder.build().toString()));

        ProcessingReport report = schema.validateUnchecked(json, true);
        OFSErrors errors = processErrors(report, objectUnderTest, "dependencies");
        assertEquals(1, errors.size());
        OFSError error = errors.getFieldError("purchase.billing_zip");
        assertEquals("test.purchase.billing_zip.required_dependency_missing", error.getCode());
    }



    // TODO Add Schema Dependency Tests




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