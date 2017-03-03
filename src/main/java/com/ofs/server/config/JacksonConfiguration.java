package com.ofs.server.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.ofs.server.json.DateSerializer;
import com.ofs.server.json.ZoneDateTimeDeserializer;
import com.ofs.server.json.ZoneDateTimeSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.ZonedDateTime;
import java.util.Date;

@Configuration
public class JacksonConfiguration {

    @Bean(name = "ofsObjectMapper")
    @Qualifier("ofsObjectMapper")
    public ObjectMapper objectMapper()
    {
        Jackson2ObjectMapperBuilder b = new Jackson2ObjectMapperBuilder();
        b.serializerByType(Date.class, new DateSerializer());
        b.deserializerByType(Date.class, new DateDeserializers.DateDeserializer());
        b.serializerByType(ZonedDateTime.class, new ZoneDateTimeSerializer());
        b.deserializerByType(ZonedDateTime.class, new ZoneDateTimeDeserializer());
        b.featuresToEnable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
        b.featuresToEnable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        b.featuresToEnable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);

        b.serializationInclusion(JsonInclude.Include.NON_NULL);

        return b.build();
    }
}
