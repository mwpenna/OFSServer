package com.ofs.examples.controller.advanced;

import com.ofs.examples.model.Person;
import com.ofs.integrationHelpers.WebIntegrationTestHelper;
import com.ofs.server.model.OFSErrors;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class AdvancedPersonControllerTest extends WebIntegrationTestHelper {

    @Test
    public void advancedPersonCreateTest() throws Exception {
        HttpHeaders headers = createHeaders();

        String request = ofsObjectMapper.writeValueAsString(generateDefaultPersonRequest());

        HttpEntity<String> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl("advanced-person"), entity, String.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void advancedPersonCreateInValidRequestTest() throws Exception {
        HttpHeaders headers = createHeaders();

        String request = ofsObjectMapper.writeValueAsString(generatePerson("Matt"));

        HttpEntity<String> entity = new HttpEntity<>(request, headers);
        ResponseEntity<OFSErrors> response = restTemplate.postForEntity(apiUrl("advanced-person"), entity, OFSErrors.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        OFSErrors errors = response.getBody();
        assertTrue(errors.getErrors().get(0).getCode().equalsIgnoreCase("person.href.not_acceptable"));
        assertTrue(errors.getErrors().get(1).getCode().equalsIgnoreCase("person.id.not_acceptable"));
    }

    @Test
    public void advancedPersonUpdateTestHasUpdates() throws Exception {
        when(personService.getPersonById(anyString())).thenReturn(Optional.of(generatePerson("Matt1")));
        HttpHeaders headers = createHeaders();

        String request = ofsObjectMapper.writeValueAsString(generateDefaultPersonRequest());

        HttpEntity<String> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl("advanced-person/id/"+id), entity, String.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void advancedPersonUpdateTestNoUpdates() throws Exception {
        when(personService.getPersonById(anyString())).thenReturn(Optional.of(generatePerson("Matt")));
        HttpHeaders headers = createHeaders();

        String request = ofsObjectMapper.writeValueAsString(generateDefaultPersonRequest());

        HttpEntity<String> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl("advanced-person/id/"+id), entity, String.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void advancedPersonUpdateTestNotFound() throws Exception {
        when(personService.getPersonById(anyString())).thenReturn(Optional.empty());
        HttpHeaders headers = createHeaders();

        String request = ofsObjectMapper.writeValueAsString(generateDefaultPersonRequest());

        HttpEntity<String> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl("advanced-person/id/"+id), entity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void advancedPersonUpdateInValidRequestTest() throws Exception {
        when(personService.getPersonById(anyString())).thenReturn(Optional.of(generatePerson("Matt")));
        HttpHeaders headers = createHeaders();

        String request = ofsObjectMapper.writeValueAsString(generatePerson("Matt"));

        HttpEntity<String> entity = new HttpEntity<>(request, headers);
        ResponseEntity<OFSErrors> response = restTemplate.postForEntity(apiUrl("advanced-person/id/"+id), entity, OFSErrors.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        OFSErrors errors = response.getBody();
        assertTrue(errors.getErrors().get(0).getCode().equalsIgnoreCase("person.href.not_acceptable"));
        assertTrue(errors.getErrors().get(1).getCode().equalsIgnoreCase("person.id.not_acceptable"));
    }

    private Person generatePerson(String name) {
        Person person = new Person();

        person.setName(name);
        person.setId(id);
        person.setHref(URI.create(apiUrl("advanced-person/id/"+id)));

        return person;
    }

}