package com.ofs.server.client;

import com.ofs.server.model.JWTSubject;
import com.ofs.server.rest.RestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URI;

@Slf4j
@Component
public class AuthClient extends RestService {

    public JWTSubject authenticate(String url, String token) {
        URI requestUri =  URI.create(url);
        HttpHeaders headers = getHeaders(token);
        RequestEntity request = new RequestEntity(headers, HttpMethod.GET, requestUri);
        return sendRequest(request);
    }

    private HttpHeaders getHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        return headers;
    }

    private JWTSubject sendRequest(RequestEntity request) {
        try {
            ResponseEntity<JWTSubject> response = exchange(request, JWTSubject.class);
            return response.getBody();
        }
        catch (Exception e) {
            log.error("Exception occurred when authenticating user: {}" + e);
            throw e;
        }
    }
}
