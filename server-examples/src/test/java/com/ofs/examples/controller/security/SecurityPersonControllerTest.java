package com.ofs.examples.controller.security;

import com.ofs.examples.model.Person;
import com.ofs.integrationHelpers.WebIntegrationTestHelper;
import com.ofs.server.model.JWTSubject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class SecurityPersonControllerTest extends WebIntegrationTestHelper {

    private UUID id;
    private UUID companyId;

    @Before
    public void setup() {
        id = UUID.randomUUID();
        companyId = UUID.randomUUID();
    }

    @Test
    public void securityPersonValidAuthenticationTest() throws Exception {
        when(authenticationClient.authenticate(any())).thenReturn(generateJWTServerSubject(JWTSubject.Role.SYSTEM_ADMIN));
        HttpHeaders headers = createHeaders("123");
        headers.setContentType(MediaType.APPLICATION_JSON);

        String request = ofsObjectMapper.writeValueAsString(generateDefaultPersonRequest());

        HttpEntity<String> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl("security-person"), entity, String.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void securityPersonInValidAuthenticationTest() throws Exception {
        when(authenticationClient.authenticate(any())).thenReturn(generateJWTServerSubject(JWTSubject.Role.ACCOUNT_MANAGER));
        HttpHeaders headers = createHeaders("123");
        headers.setContentType(MediaType.APPLICATION_JSON);

        String request = ofsObjectMapper.writeValueAsString(generateDefaultPersonRequest());

        HttpEntity<String> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl("security-person"), entity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private JWTSubject generateJWTServerSubject(JWTSubject.Role role) {
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

    private Person generateDefaultPersonRequest() {
        Person person = new Person();
        person.setName("Matt");
        return person;
    }
}