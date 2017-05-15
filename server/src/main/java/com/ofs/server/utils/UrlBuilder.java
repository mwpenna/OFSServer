package com.ofs.server.utils;

import java.net.URI;
import java.net.URL;

public class UrlBuilder {
    private String userinfo;
    private String scheme;
    private String host;
    private int port = -1;
    private String path;
    private String hash;
    private String query;

    private UrlBuilder(String scheme) {
        if(scheme.length() < 1) {
            throw new IllegalArgumentException("empty scheme");
        } else {
            for(int i = 0; i < scheme.length(); ++i) {
                char c = scheme.charAt(i);
                if(!Character.isLetterOrDigit(c) && c != 43 && c != 45 && c != 46) {
                    throw new IllegalArgumentException("invalid characters found in scheme");
                }
            }

            this.scheme = scheme.toLowerCase();
        }
    }

    public String getScheme() {
        return this.scheme;
    }

    public String getUserInfo() {
        return this.userinfo;
    }

    public UrlBuilder setUserInfo(String userinfo) {
        this.userinfo = Strings.nullIfEmpty(userinfo);
        return this;
    }

    public UrlBuilder setUserInfo(String user, String pass) {
        if(Strings.isEmpty(user)) {
            throw new IllegalArgumentException("user can not be empty");
        } else {
            return pass == null?this.setUserInfo(user):this.setUserInfo(user + ":" + pass);
        }
    }

    public String getHost() {
        return this.host;
    }

    public UrlBuilder setHost(String host) {
        this.host = Strings.nullIfEmpty(host);
        return this;
    }

    public int getPort() {
        return this.port;
    }

    public UrlBuilder setPort(int port) {
        if(port >= -1 && port <= '\uffff') {
            this.port = port;
            return this;
        } else {
            throw new IllegalArgumentException("invalid port value: " + port);
        }
    }

    public String getPath() {
        return this.path;
    }

    public UrlBuilder setPath(String path) {
        this.path = Strings.nullIfEmpty(path);
        return this;
    }

    public String getFragment() {
        return this.hash;
    }

    public UrlBuilder setFragment(String hash) {
        this.hash = Strings.nullIfEmpty(hash);
        return this;
    }

    public String getQuery() {
        return this.query;
    }

    public UrlBuilder setQuery(String query) {
        this.query = Strings.nullIfEmpty(query);
        return this;
    }

    public String build() {
        StringBuilder buf = (new StringBuilder(this.scheme)).append(":");
        String authority = this.createAuthority();
        if(authority != null) {
            buf.append(authority);
            if(this.path == null || !this.path.startsWith("/")) {
                buf.append("/");
            }
        }

        if(this.path != null) {
            buf.append(this.path);
        }

        if(this.query != null) {
            buf.append("?").append(this.query);
        }

        if(this.hash != null) {
            buf.append("#").append(this.hash);
        }

        return buf.toString();
    }

    private String createAuthority() {
        if(!Strings.isEmpty(this.host)) {
            StringBuilder buf = new StringBuilder("//");
            if(this.userinfo != null) {
                buf.append(this.userinfo).append("@");
            }

            buf.append(this.host);
            if(this.port != -1) {
                buf.append(":").append(this.port);
            }

            return buf.toString();
        } else {
            return null;
        }
    }

    public UrlBuilder clone() {
        UrlBuilder builder = new UrlBuilder(this.scheme);
        builder.setUserInfo(this.userinfo).setHost(this.host).setPort(this.port);
        builder.setPath(this.path).setQuery(this.query).setFragment(this.hash);
        return builder;
    }

    public static UrlBuilder create(String scheme) {
        return new UrlBuilder((String)Objects.notNull(scheme, "scheme"));
    }

    public static UrlBuilder create(URL url) {
        UrlBuilder builder = new UrlBuilder(url.getProtocol());
        builder.setUserInfo(url.getUserInfo()).setHost(url.getHost()).setPort(url.getPort());
        builder.setPath(url.getPath()).setQuery(url.getQuery()).setFragment(url.getRef());
        return builder;
    }

    public static UrlBuilder create(URI url) {
        UrlBuilder builder = new UrlBuilder(url.getScheme());
        builder.setUserInfo(url.getUserInfo()).setHost(url.getHost()).setPort(url.getPort());
        builder.setPath(url.getPath()).setQuery(url.getQuery()).setFragment(url.getFragment());
        return builder;
    }
}

