package com.ofs.server.model;

import lombok.Data;

import java.net.URI;

@Data
public class JWTSubject {

    public enum Role {
        ADMIN,
        ACCOUNT_MANAGER,
        WAREHOUSE,
        CUSTOMER
    }

    URI href;
    URI companyHref;
    String firstName;
    String lastName;
    Role role;
    String userName;
    String emailAddress;
}
