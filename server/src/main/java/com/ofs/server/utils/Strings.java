package com.ofs.server.utils;

import java.util.Iterator;

public final class Strings {
    private Strings() {
    }

    public static String nullIfEmpty(String str) {
        return isEmpty(str)?null:str;
    }

    public static String emptyIfNull(String str) {
        return str == null?"":str;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static String join(String sep, String... parts) {
        return join(sep, parts, 0, parts.length);
    }

    public static String join(String sep, String[] parts, int offset, int length) {
        if (sep == null) {
            throw new NullPointerException("sep");
        } else if (parts == null) {
            throw new NullPointerException("parts");
        } else {
            StringBuilder buf = new StringBuilder();

            for (int i = offset; i < offset + length; ++i) {
                if (parts[i] != null) {
                    if (buf.length() > 0) {
                        buf.append(sep);
                    }

                    buf.append(parts[i]);
                }
            }

            return buf.toString();
        }
    }

    public static String join(String sep, Iterable<String> parts) {
        if (sep == null) {
            throw new NullPointerException("sep");
        } else if (parts == null) {
            throw new NullPointerException("parts");
        } else {
            StringBuilder buf = new StringBuilder();
            Iterator i$ = parts.iterator();

            while (i$.hasNext()) {
                Object part = i$.next();
                if (part != null) {
                    if (buf.length() > 0) {
                        buf.append(sep);
                    }

                    buf.append(part);
                }
            }

            return buf.toString();
        }
    }

    public static String firstCharToUpper(String str) {
        return str != null && str.length() >= 1?str.substring(0, 1).toUpperCase() + str.substring(1):str;
    }

    public static String notEmpty(String arg, String argName) {
        if(isEmpty(arg)) {
            throw new IllegalArgumentException(String.format("%s is empty", new Object[]{argName}));
        } else {
            return arg;
        }
    }
}
