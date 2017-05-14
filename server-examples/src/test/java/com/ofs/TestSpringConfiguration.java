package com.ofs;

import com.ofs.examples.service.PersonService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.mock;

@Configuration
public class TestSpringConfiguration {

    @Bean
    public RestTemplate restTemplate() { return new RestTemplate(); }

    @Primary
    @Bean
    public PersonService personService() { return mock(PersonService.class);}
}
