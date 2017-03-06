package com.ofs.server.security;

import xpertss.lang.Objects;
import xpertss.lang.Strings;
import xpertss.util.Sets;

import java.util.Collections;
import java.util.Set;

public class Subject {
    private final String username;
    private final String domain;
    private final String system;
    private final String token;

    private final Set<Integer> accounts;
    private final Set<String> roles;


    private Subject(Builder builder)
    {
        this.username = Objects.notNull(builder.username, "username");
        this.domain = Strings.ifEmpty(builder.domain, "manheim-employee");
        this.system = Strings.ifEmpty(builder.system, "unknown");
        this.token = builder.token;
        this.accounts = Collections.unmodifiableSet(builder.accounts);
        this.roles = Collections.unmodifiableSet(builder.roles);
    }

    /**
     * Unique identifier for this entity that can be used in a backing store
     * to denote the owner/creator of a cookbook entity.
     * <p/>
     * This is a combination of the domain and username.
     */
    public String id()
    {
        return String.format("%s:%s", domain, username);
    }

    /**
     * The domain specific username.
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * The domain to which the username belongs.
     */
    public String getDomain()
    {
        return domain;
    }

    /**
     * The system through which the entity has obtained access.
     */
    public String getSystem()
    {
        return system;
    }


    /**
     * Returns {@code true} if the user belongs to the specified role.
     * <p/>
     * These loosely map to the scopes bound to the entity's bearer token.
     */
    public boolean isUserInRole(String role)
    {
        return roles.contains(role);
    }

    /**
     * Returns {@code true} if the user is a customer and their online account is linked
     * to the given auction access dealership number.
     * <p/>
     * NOT YET IMPLEMENTED
     */
    public boolean isUserAssociatedWith(int account)
    {
        if(true) throw new UnsupportedOperationException("not yet implemented");
        return accounts.contains(account);
    }


    /**
     * Returns the bearer token provided by the caller. This will be {@code null} if the
     * request bypasses Mashery and does not provide a bearer token.
     */
    public String getToken()
    {
        return token;
    }



    @Override
    public boolean equals(Object another)
    {
        if(another instanceof Subject) {
            Subject o = (Subject) another;
            return Objects.equal(username, o.username)
                    && Objects.equal(domain, o.domain);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(username, domain);
    }


    static Builder user(String username) { return new Builder().user(username); }

    static class Builder {
        private String username;
        private String domain;
        private String system;
        private String token;

        private Set<Integer> accounts = Sets.newHashSet();
        private Set<String> roles = Sets.newHashSet();

        public Builder user(String username)
        {
            this.username = username;
            return this;
        }

        public Builder domain(String domain)
        {
            this.domain = domain;
            return this;
        }

        public Builder system(String system)
        {
            this.system = system;
            return this;
        }

        public Builder token(String token)
        {
            this.token = token;
            return this;
        }

        public Builder add(String role)
        {
            if(!Strings.isEmpty(role)) this.roles.add(role);
            return this;
        }

        public Builder addAll(Set<String> roles)
        {
            if(roles != null) this.roles.addAll(roles);
            return this;
        }

        public Builder add(int account)
        {
            this.accounts.add(account);
            return this;
        }

        public Subject build() { return new Subject(this); }

    }
}
