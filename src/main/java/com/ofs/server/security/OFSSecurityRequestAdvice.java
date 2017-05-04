package com.ofs.server.security;

import com.ofs.server.OFSController;
import com.ofs.server.client.AuthenticationClient;
import com.ofs.server.errors.ForbiddenException;
import com.ofs.server.model.JWTSubject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import feign.FeignException;

@ControllerAdvice(annotations = OFSController.class)
public class OFSSecurityRequestAdvice implements RequestBodyAdvice {

    @Autowired
    AuthenticationClient authenticationClient;

    @Override
    public boolean supports(MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType)
    {
        if(parameter.hasParameterAnnotation(Authenticate.class)) {
            return true;
        }

        return false;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage input, MethodParameter parameter, Type targetType,
                                  Class<? extends HttpMessageConverter<?>> converterType)
    {
        return body;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage input, MethodParameter parameter, Type targetType,
                                           Class<? extends HttpMessageConverter<?>> converterType)
            throws IOException
    {
        String authString = getValidAuthHeader(input.getHeaders());
        String token = getBeaerTokenFromAuthentication(authString);
        SecurityContext.bind(createSubject(authenticateUser(token), token));
        return input;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage input, MethodParameter parameter, Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType)
    {
        return body;
    }

    private JWTSubject authenticateUser(String authToken) {
        JWTSubject subject;
        try{
            subject = authenticationClient.authenticate(authToken);
        }
        catch (FeignException ex) {
            if(ex.status() == 403) {
                throw new ForbiddenException();
            }

            throw ex;
        }

        return subject;
    }

    private Subject createSubject(JWTSubject jwtSubject, String token) {
        return Subject.href(jwtSubject.getHref())
                .companyHref(jwtSubject.getCompanyHref())
                .firstName(jwtSubject.getFirstName())
                .lastName(jwtSubject.getLastName())
                .role(jwtSubject.getRole().toString())
                .userName(jwtSubject.getUserName())
                .emailAddress(jwtSubject.getUserName())
                .token(token)
                .build();
    }

    private String getValidAuthHeader(HttpHeaders headers) {

        List<String> authHeader = headers.get("Authorization");
        if(!validateAuthorizationHeader(authHeader)) {
            throw new ForbiddenException();
        }

        String authString = headers.get("Authorization").get(0);
        if(!validateAuthString(authString)) {
            throw new ForbiddenException();
        }

        return authString;
    }

    private boolean validateAuthString(String authString) {
        if(authString == null || authString.split(" ").length < 2) {
            return false;
        }

        return true;
    }

    private boolean validateAuthorizationHeader(List<String> authHeader) {
        if(authHeader == null || authHeader.isEmpty()) {
            return false;
        }

        return true;
    }

    private String getBeaerTokenFromAuthentication(String authString) {
        String[] authentication = authString.split(" ");

        if(!authentication[0].equalsIgnoreCase("Bearer")) {
            throw new ForbiddenException();
        }

        return authentication[1];
    }
}
