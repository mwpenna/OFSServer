package com.ofs.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ofs.server.spring.OFSErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.xml.transform.Source;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestConfiguration {

    @Autowired
    @Qualifier("ofsObjectMapper")
    private ObjectMapper ofsObjectMapper;

    @Bean
    @ConfigurationProperties(prefix = "rest.connection")
    public SimpleClientHttpRequestFactory customHttpRequestFactory() {
        return new SimpleClientHttpRequestFactory();
    }

    @Bean
    public RestTemplate restTemplate() {

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new ByteArrayHttpMessageConverter());
        messageConverters.add(new StringHttpMessageConverter());
        messageConverters.add(new ResourceHttpMessageConverter());
        messageConverters.add(new SourceHttpMessageConverter<Source>());
        messageConverters.add(new AllEncompassingFormHttpMessageConverter());
        messageConverters.add(new MappingJackson2HttpMessageConverter(ofsObjectMapper));

        RestTemplate template = new RestTemplate(messageConverters);
        template.setRequestFactory(customHttpRequestFactory());
        template.setErrorHandler(new OFSErrorHandler());
        return template;
    }
}
