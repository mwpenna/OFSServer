package com.ofs.examples.utils;


import java.net.URI;

public class StringUtils {

    public static String getIdFromURI(URI href) {
        if (href == null) return null;
        return getPart(href.toString());
    }

    public static String getIdFromURIString(String href) {
        if (org.apache.commons.lang.StringUtils.isBlank(href)) return null;
        return getPart(href);
    }

    private static String getPart(String string) {
        String[] parts = string.split("id/");
        if (parts.length > 1) {
            return parts[1];
        }
        return null;
    }

    public static String getTyoeFromURIString(String href) {
        if (org.apache.commons.lang.StringUtils.isBlank(href)) return null;
        return getType(href);
    }

    private static String getType(String string) {
        String[] parts = string.split("offerings/");
        if (parts.length > 1) {
            return parts[1];
        }
        return null;
    }
}
