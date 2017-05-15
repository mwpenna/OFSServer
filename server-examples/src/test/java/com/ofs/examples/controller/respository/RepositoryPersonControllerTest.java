package com.ofs.examples.controller.respository;

import com.ofs.examples.model.Person;
import com.ofs.integrationHelpers.WebIntegrationTestHelper;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class RepositoryPersonControllerTest extends WebIntegrationTestHelper {

    @Test
    public void repositoryPersonCreateTest() throws Exception {
        HttpHeaders headers = createHeaders("123");
        headers.setContentType(MediaType.APPLICATION_JSON);

        String request = ofsObjectMapper.writeValueAsString(generateDefaultPersonRequest());

        HttpEntity<String> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl("repository-person"), entity, String.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void repositoryPersonGetByIdTest() throws Exception {
        when(personRepository.getPersonById(any())).thenReturn(Optional.of(generatePerson()));

        HttpHeaders headers = createHeaders("123");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(apiUrl("repository-person/id/"+id), HttpMethod.GET,entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void repositoryPersonDeleteIdTest() {
        HttpHeaders headers = createHeaders("123");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(apiUrl("repository-person/id/"+id), HttpMethod.DELETE, entity, String.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void repositoryPersonUpdateTest() throws Exception {
        when(personRepository.getPersonById(anyString())).thenReturn(Optional.of(generatePerson()));

        HttpHeaders headers = createHeaders("123");
        headers.setContentType(MediaType.APPLICATION_JSON);

        String request = ofsObjectMapper.writeValueAsString(generateDefaultPersonRequest());

        HttpEntity<String> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl("repository-person/id/"+id), entity, String.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    private Person generatePerson() {
        Person person = new Person();

        person.setName("Matt1");
        person.setHref(URI.create(apiUrl("repository-person/id/"+id)));
        person.setId(id);
        return person;
    }
}