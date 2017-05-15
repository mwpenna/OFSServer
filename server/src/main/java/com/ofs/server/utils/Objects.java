package com.ofs.server.utils;

public final class Objects {
    private Objects() {
    }

    @SafeVarargs
    public static <T> boolean isOneOf(T obj, T... set) {
        if (set != null) {
            Object[] arr$ = set;
            int len$ = set.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                Object item = arr$[i$];
                if (obj == null) {
                    if (item == null) {
                        return true;
                    }
                } else if (obj.equals(item)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String toString(Object o) {
        return o == null ? null : o.toString();
    }

    public static String toString(Object o, String nullDefault) {
        return o != null ? o.toString() : nullDefault;
    }

    public static <T> T notNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        } else {
            return obj;
        }
    }

    public static <T> T notNull(T obj, String argName) {
        if (obj == null) {
            throw new NullPointerException(argName);
        } else {
            return obj;
        }
    }

    public static boolean equal(Object... items) {
        if(((Object[])notNull(items)).length < 1) {
            return false;
        } else {
            Object first = items[0];

            for(int i = 1; i < items.length; ++i) {
                if(first == null) {
                    if(items[i] != null) {
                        return false;
                    }
                } else if(!first.equals(items[i])) {
                    return false;
                }
            }

            return true;
        }
    }

    public static <T> T ifNull(T obj, T def) {
        return obj == null?def:obj;
    }
}

