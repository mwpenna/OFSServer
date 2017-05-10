package com.ofs.server.utils;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

import static java.lang.String.format;

/**
 * Utility methods for building URI links. These methods take into account HTTP
 * proxy headers which mask the original request scheme, host, port, etc.
 */
public final class Links {

    /**
     * Generate an ID uri from the current request path and given id. This assumes
     * the request path is currently pointing at the creator path which is the base
     * entity path.
     */
    public static URI generateIdUri(HttpServletRequest request, Object id)
    {
        return generateIdUri(request, request.getRequestURI(), id);
    }

    /**
     * Generate an ID uri from the specified entity path and given id.
     */
    public static URI generateIdUri(HttpServletRequest request, String path, Object id)
    {
        return URI.create(format("%s%s/id/%s", generateAuthority(request), normalizePath(path), id.toString()));
    }


    /**
     * Utility method that normalizes paths for concatenation. It removes trailing {@code /}
     * characters and ensures the path begins with a {@code /} character.
     */
    public static String normalizePath(String path)
    {
        if(path.endsWith("/")) path = path.substring(0, path.length() - 1);
        return (path.startsWith("/")) ? path : format("/%s", path);
    }

    /**
     * Constructs the scheme, host, port portion of a URI from the given request data.
     */
    public static String generateAuthority(HttpServletRequest request)
    {
        int port = request.getServerPort();
        String base = format("%s://%s", request.getScheme(), request.getServerName());
        if(port != 0 && !isDefaultPort(request.getScheme(), port)) {
            return format("%s:%d", base, port);
        }
        return base;
    }

    private static boolean isDefaultPort(String scheme, int port)
    {
        return (port == 80 && "http".equals(scheme)) || (port == 443 && "https".equals(scheme));
    }

}