package com.ofs.server.page;


import xpertss.lang.Integers;
import xpertss.lang.Numbers;

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
        this.start = Numbers.gte(0, start, "start");
        this.limit = Numbers.gt(0, limit, "limit");
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
        int start = Integers.max(0, Integers.parse(request.getParameter("start"), -1));
        int limit = Integers.parse(request.getParameter("limit"), 25);
        limit = Integers.min(limit, maxLimit);
        if(limit < 1) limit = 25;
        return new Page(start, limit);
    }
}
