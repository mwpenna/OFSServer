package com.ofs.server.client;

import com.ofs.server.model.JWTSubject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@FeignClient("http://USERSERVICE")
public interface AuthenticationClient {
    @RequestMapping(value = "/users/authenticate", method= GET)
    JWTSubject authenticate(@RequestHeader("Authorization") String token);
}
