package com.ofs;

import com.ofs.examples.repository.PersonRepository;
import com.ofs.examples.service.PersonService;
import com.ofs.server.client.AuthClient;
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

    @Primary
    @Bean
    public PersonRepository personRepository() { return mock(PersonRepository.class); }

    @Primary
    @Bean
    public AuthClient authClient() { return mock(AuthClient.class); }
}
