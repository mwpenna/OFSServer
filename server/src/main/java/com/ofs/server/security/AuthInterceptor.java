package com.ofs.server.security;

import com.ofs.server.client.AuthClient;
import com.ofs.server.errors.ForbiddenException;
import com.ofs.server.model.JWTSubject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    AuthClient authClient;

    @Value("${AUTH_CLIENT_URL:http://localhost:8081/users/authenticate}")
    private String authClientUrl= "http://localhost:8081/users/authenticate";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            if(handlerMethod.getMethod().isAnnotationPresent(Authenticate.class)) {
                String authString = validAuthHeader(request.getHeader("Authorization"));
                String token = getBeaerTokenFromAuthentication(authString);
                SecurityContext.bind(createSubject(authenticateUser(authString), token));
            }

        }
        return true;
    }

    private JWTSubject authenticateUser(String authToken) {
        JWTSubject subject;
        try{
            subject = authClient.authenticate(authClientUrl, authToken);
        }
        catch (Exception e) {
            log.error("Exception occurend when trying to authenticate the user: {}", e);
            throw new ForbiddenException();
        }
//        catch (FeignException ex) {
//            if(ex.status() == 403) {
//                log.error("User unathorized", ex);
//                throw new ForbiddenException();
//            }
//
//            log.error("Unexspected error occured when trying to authenticate user", ex);
//            throw ex;
//        }

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

    private String validAuthHeader(String authHeader) {

        if(!validateAuthorizationHeader(authHeader)) {
            throw new ForbiddenException();
        }

        if(!validateAuthString(authHeader)) {
            throw new ForbiddenException();
        }

        return authHeader;
    }

    private boolean validateAuthString(String authString) {
        if(authString == null || authString.split(" ").length < 2) {
            return false;
        }

        return true;
    }

    private boolean validateAuthorizationHeader(String authHeader) {
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
