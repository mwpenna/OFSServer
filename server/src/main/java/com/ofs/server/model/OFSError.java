package com.ofs.server.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;


@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OFSError {
    @JsonProperty
    private String code;

    @JsonProperty
    private String property;

    @JsonProperty
    private String message;

    @JsonProperty
    private String developerMessage;

    @JsonProperty
    private Map<String,String> properties = new LinkedHashMap<>();

    public OFSError() { }

    OFSError(String code, String developerMessage)
    {
        this.code = code;
        this.developerMessage = developerMessage;
    }

    OFSError(String code, String field, String developerMessage)
    {
        this(code, developerMessage);
        if(!StringUtils.isEmpty(field)) {
            this.property = field;
            properties.put("field", field);
        }
    }


    public String getCode()
    {
        return code;
    }

    public String getProperty()
    {
        return property;
    }

    public String getMessage()
    {
        return message;
    }

    public String getDeveloperMessage()
    {
        return developerMessage;
    }


    public OFSError put(String name, String value)
    {
        properties.put(name, value);
        return this;
    }


    public Map<String,String> getProperties()
    {
        return Collections.unmodifiableMap(properties);
    }



    @Override
    public int hashCode()
    {
        return Objects.hash(code);
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof OFSError) {
            OFSError o = (OFSError) obj;
            return Objects.equals(code, o.code);
        }
        return false;
    }


    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append(code);
        return buf.toString();
    }
}
