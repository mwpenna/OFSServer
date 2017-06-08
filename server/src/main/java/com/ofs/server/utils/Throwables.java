package com.ofs.server.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Throwables {
    private Throwables() {
    }

    public static void propagate(Throwable throwable) {
        if(throwable instanceof Error) {
            throw (Error)throwable;
        } else if(throwable instanceof RuntimeException) {
            throw (RuntimeException)throwable;
        } else if(throwable == null) {
            throw new NullPointerException();
        } else {
            throw new RuntimeException(throwable);
        }
    }

    public static Throwable getRootCause(Throwable t) {
        while(t.getCause() != null) {
            t = t.getCause();
        }

        return t;
    }

    public static <T> T getFirstOfType(Throwable t, Class<T> type) {
        while(t != null && t.getClass() != type) {
            t = t.getCause();
        }

        return type.cast(t);
    }

    public static String toShortString(Throwable t) {
        while(t.getCause() != null) {
            t = t.getCause();
        }

        StringBuilder buf = new StringBuilder();
        buf.append(stripPackage(t.getClass().getName()));
        if(!Strings.isEmpty(t.getMessage())) {
            buf.append(" - ").append(t.getMessage());
        }

        StackTraceElement[] ste = t.getStackTrace();

        for(int i = 0; i < Math.min(ste.length, 5); ++i) {
            buf.append(System.getProperty("line.separator"));
            buf.append("   ").append(stripPackage(ste[i].getClassName()));
            buf.append(".").append(ste[i].getMethodName());
            if(ste[i].isNativeMethod()) {
                buf.append("(Native Method)");
            } else if(ste[0].getLineNumber() >= 0) {
                buf.append("(line:").append(ste[i].getLineNumber()).append(")");
            } else {
                buf.append("(Unknown Source)");
            }
        }

        return buf.toString();
    }

    public static String toString(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw, true));
        return sw.toString();
    }

    public static boolean isChecked(Throwable t) {
        return t != null && isChecked(t.getClass());
    }

    public static boolean isChecked(Class<?> exceptionType) {
        return exceptionType != null && Throwable.class.isAssignableFrom(exceptionType) && !Error.class.isAssignableFrom(exceptionType) && !RuntimeException.class.isAssignableFrom(exceptionType);
    }

    private static String stripPackage(String name) {
        return name.substring(name.lastIndexOf(46) + 1);
    }
}
