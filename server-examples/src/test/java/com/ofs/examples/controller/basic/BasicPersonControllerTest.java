package com.ofs.examples.controller.basic;

import com.ofs.integrationHelpers.WebIntegrationTestHelper;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.*;

public class BasicPersonControllerTest extends WebIntegrationTestHelper {

    @Test
    public void basicPersonCreateTest() throws Exception {
        HttpHeaders headers = createHeaders();

        String request = ofsObjectMapper.writeValueAsString(generateDefaultPersonRequest());

        HttpEntity<String> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl("basic-person"), entity, String.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}
