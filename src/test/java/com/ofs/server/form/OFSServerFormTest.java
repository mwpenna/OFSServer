package com.ofs.server.form;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.ofs.server.config.JacksonConfiguration;
import com.ofs.server.model.BaseOFSEntity;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xpertss.json.util.Strings;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class OFSServerFormTest {

    private static ObjectMapper jackson;

    @BeforeClass
    public static void init()
    {
        JacksonConfiguration config = new JacksonConfiguration();
        jackson = config.objectMapper();
    }

    @Test
    public void testJsonNodePath()
    {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode object = mapper.createObjectNode();
        String str = object.path("keyword").asText();
        assertTrue(Strings.isEmpty(str));
    }

    @Test
    public void testGetRequestPropertyThirdLevel()
    {
        ObjectNode requestBody = jackson.createObjectNode();
        ObjectNode base = requestBody.putObject("base");
        ObjectNode middle = base.putObject("middle");
        middle.put("testField", "value");

        OFSServerForm<TestEntity> objectUnderTest = new OFSServerForm<>(null, requestBody, TestEntity.class);

        ObjectNode obj = objectUnderTest.findProperty("$.base.middle");
        assertEquals(1, obj.size());

        TextNode text =  objectUnderTest.findProperty("$.base.middle.testField");
        assertEquals("value", text.asText());

        assertNull(objectUnderTest.findProperty("$.doesNotExist"));
        assertNull(objectUnderTest.findProperty("$.base.doesNotExist"));
        assertNull(objectUnderTest.findProperty("$.base.middle.doesNotExist"));
        assertNull(objectUnderTest.findProperty("$.base.middle[0].doesNotExist"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreate_shouldThrowIllegalArgumentException() throws IOException {
        ObjectNode requestBody = jackson.createObjectNode();
        ObjectNode base = requestBody.putObject("base");
        ObjectNode middle = base.putObject("middle");
        middle.put("testField", "value");

        OFSServerForm<TestEntity> objectUnderTest = new OFSServerForm<>(null, requestBody, TestEntity.class);

        objectUnderTest.create(new FakeEntity());
    }

    @Test
    public void testJaywayJsonPath()
    {
        ObjectNode requestBody = jackson.createObjectNode();
        ObjectNode base = requestBody.putObject("base");
        ObjectNode middle = base.putObject("middle");
        middle.put("testField", "value");



        Configuration conf = Configuration.builder()
                .jsonProvider(new JacksonJsonNodeJsonProvider())
                .build();
        DocumentContext document = JsonPath.using(conf).parse(requestBody);

        JsonNode nodeOne =  document.read("$.base.middle");
        assertTrue(nodeOne instanceof ObjectNode);

        JsonNode nodeTwo =  document.read("$.base.middle.testField");
        assertTrue(nodeTwo instanceof TextNode);
        assertEquals("value", nodeTwo.asText());
    }

    public static final class TestEntity extends BaseOFSEntity {

    }

    public static final class FakeEntity {
        private String someField;
    }
}
