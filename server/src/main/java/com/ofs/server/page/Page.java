package com.ofs.server.page;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Objects;

public class Page implements Serializable, Cloneable {

    private int start;
    private int limit;

    private Page(int limit)
    {
        this(0, limit);
    }

    private Page(int start, int limit)
    {
        this.start = gte(0, start, "start");
        this.limit = gt(0, limit, "limit");
    }

    private int gte(int minimum, int arg, String argName) {
        if(((Comparable)minimum).compareTo(arg) > 0) {
            throw new IllegalArgumentException(String.format("%s(%s) not greater than equal to %s", new Object[]{argName, arg, minimum}));
        } else {
            return arg;
        }
    }

    private int gt(int minimum, int arg, String argName) {
        if(((Comparable)minimum).compareTo(arg) >= 0) {
            throw new IllegalArgumentException(String.format("%s(%s) not greater than %s", new Object[]{argName, arg, minimum}));
        } else {
            return arg;
        }
    }


    public int getStart()
    {
        return start;
    }

    public int getLimit()
    {
        return limit;
    }

    public boolean equals(Object obj)
    {
        if(obj instanceof Page) {
            Page o = (Page) obj;
            return Objects.equals(start, o.start) &&
                    Objects.equals(limit, o.limit);
        }
        return false;
    }

    public int hashCode()
    {
        return Objects.hash(start, limit);
    }

    public String toString()
    {
        return String.format("Page{start=%d, limit=%d}", start, limit);
    }

    public static Page from(HttpServletRequest request, int maxLimit)
    {
        int start = max(0, parse(request.getParameter("start"), -1));
        int limit = parse(request.getParameter("limit"), 25);
        limit = min(limit, maxLimit);
        if(limit < 1) limit = 25;
        return new Page(start, limit);
    }

    private static int min(int... items) {
        int min = notEmpty(items)[0];

        for(int i = 1; i < items.length; ++i) {
            min = Math.min(min, items[i]);
        }

        return min;
    }

    private static int parse(CharSequence str, int def) {
        try {
            return Integer.parseInt(toString(str));
        } catch (Exception var3) {
            return def;
        }
    }

    private static String toString(CharSequence seq) {
        return seq != null?seq.toString():null;
    }

    private static int max(int... items) {
        int max = notEmpty(items)[0];

        for(int i = 1; i < items.length; ++i) {
            max = Math.max(max, items[i]);
        }

        return max;
    }

    private static int[] notEmpty(int[] array) {
        if(array == null) {
            throw new NullPointerException();
        } else if(array.length < 1) {
            throw new IllegalArgumentException();
        } else {
            return array;
        }
    }
}
