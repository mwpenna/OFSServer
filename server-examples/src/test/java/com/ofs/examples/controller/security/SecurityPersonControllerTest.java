package com.ofs.examples.controller.security;

import com.ofs.integrationHelpers.WebIntegrationTestHelper;
import com.ofs.server.model.JWTSubject;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class SecurityPersonControllerTest extends WebIntegrationTestHelper {

    @Test
    public void securityPersonValidAuthenticationTest() throws Exception {
        when(authClient.authenticate(any(), any())).thenReturn(generateJWTServerSubject(JWTSubject.Role.SYSTEM_ADMIN));
        HttpHeaders headers = createHeaders("123");
        headers.setContentType(MediaType.APPLICATION_JSON);

        String request = ofsObjectMapper.writeValueAsString(generateDefaultPersonRequest());

        HttpEntity<String> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl("security-person"), entity, String.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void securityPersonInValidAuthenticationTest() throws Exception {
        when(authClient.authenticate(any(), any())).thenReturn(generateJWTServerSubject(JWTSubject.Role.ACCOUNT_MANAGER));
        HttpHeaders headers = createHeaders("123");
        headers.setContentType(MediaType.APPLICATION_JSON);

        String request = ofsObjectMapper.writeValueAsString(generateDefaultPersonRequest());

        HttpEntity<String> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl("security-person"), entity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}