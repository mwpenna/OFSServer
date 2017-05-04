package com.ofs.server.security;

import java.net.URI;

public class Subject {
    private final URI href;
    private final URI companyHref;
    private final String firstName;
    private final String lastName;
    private final String role;
    private final String userName;
    private final String emailAddress;
    private final String token;


    private Subject(Builder builder)
    {
        this.href = builder.href;
        this.companyHref = builder.companyHref;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.role = builder.role;
        this.userName = builder.userName;
        this.emailAddress = builder.emailAddress;
        this.token = builder.token;
    }

    public URI getHref() { return href; }

    public URI getCompanyHref() { return companyHref; }

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public String getRole() { return role; }

    public String getUserName() { return userName; }

    public String getEmailAddress() { return emailAddress;  }

    public String getToken()
    {
        return token;
    }

    static Builder href(URI href) { return new Builder().href(href); }

    static class Builder {
        private URI href;
        private URI companyHref;
        private String firstName;
        private String lastName;
        private String role;
        private String userName;
        private String emailAddress;
        private String token;

        public Builder href(URI href)
        {
            this.href = href;
            return this;
        }

        public Builder companyHref(URI companyHref)
        {
            this.companyHref = companyHref;
            return this;
        }

        public Builder firstName(String firstName)
        {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName)
        {
            this.lastName = lastName;
            return this;
        }

        public Builder role(String role)
        {
            this.role = role;
            return this;
        }

        public Builder userName(String userName)
        {
            this.userName = userName;
            return this;
        }

        public Builder emailAddress(String emailAddress)
        {
            this.emailAddress = emailAddress;
            return this;
        }

        public Builder token(String token)
        {
            this.token = token;
            return this;
        }

        public Subject build() { return new Subject(this); }

    }
}
