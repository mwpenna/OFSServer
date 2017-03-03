package com.ofs.server.form.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ofs.server.form.schema.JsonSchema;
import org.springframework.http.server.ServletServerHttpRequest;

public class RequestContext {

    private final ErrorDigesterFactory factory;
    private final ObjectMapper mapper;

    private ServletServerHttpRequest requestBody;
    private JsonSchema schema;
    private String entityName;


    public RequestContext(ErrorDigesterFactory factory, ObjectMapper mapper)
    {
        this.factory = factory;
        this.mapper = mapper;
    }



    public ErrorDigesterFactory getFactory()
    {
        return factory;
    }

    public ObjectMapper getMapper()
    {
        return mapper;
    }



    public void setRequestBody(ServletServerHttpRequest requestBody)
    {
        this.requestBody = requestBody;
    }

    public ServletServerHttpRequest getRequestBody()
    {
        return requestBody;
    }



    public void setEntityName(String entityName)
    {
        this.entityName = entityName;
    }

    public String getEntityName()
    {
        return entityName;
    }



    public void setSchema(JsonSchema schema)
    {
        this.schema = schema;
    }

    public JsonSchema getSchema()
    {
        return schema;
    }

}
