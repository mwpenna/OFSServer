package com.ofs.integrationHelpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ofs.examples.controller.service.PersonService;
import com.ofs.examples.model.Person;
import com.ofs.server.client.AuthenticationClient;
import com.ofs.server.model.JWTSubject;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class WebIntegrationTestHelper {

    @Value("${local.server.port}")
    public int port;

    @Autowired
    public RestTemplate restTemplate;

    @Autowired
    @Qualifier("ofsObjectMapper")
    public ObjectMapper ofsObjectMapper;

    @Autowired
    public PersonService personService;

    @MockBean
    public AuthenticationClient authenticationClient;

    protected UUID id;
    protected UUID companyId;

    @Before
    public void superSetup() {

        id = UUID.randomUUID();
        companyId = UUID.randomUUID();

        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response)
                    throws IOException
            {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response)
                    throws IOException
            {

            }
        });
    }

    protected HttpHeaders createHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization" , "Bearer " + token);
        return headers;
    }

    protected HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public String apiUrl(String path) {
        return "http://localhost:" + port + "/" + path;
    }

    protected JWTSubject generateJWTServerSubject(JWTSubject.Role role) {
        com.ofs.server.model.JWTSubject subject = new com.ofs.server.model.JWTSubject();

        subject.setHref(URI.create(apiUrl("/users/id/"+id)));
        subject.setFirstName("name");
        subject.setLastName("lName");
        subject.setRole(role);
        subject.setUserName("name.lName");
        subject.setEmailAddress("name.lName@place.com");
        subject.setCompanyHref(URI.create(apiUrl("company/id/" + companyId)));

        return subject;
    }

    protected Person generateDefaultPersonRequest() {
        Person person = new Person();
        person.setName("Matt");
        return person;
    }
}