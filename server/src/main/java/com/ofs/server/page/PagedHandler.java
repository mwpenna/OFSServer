package com.ofs.server.page;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;

public class PagedHandler implements InvocationHandler {

    private final Collection proxied;
    private final boolean hasNext;

    PagedHandler(Collection proxied, boolean hasNext)
    {
        this.proxied = proxied;
        this.hasNext = hasNext;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable
    {
        if ("hasNext".equals(method.getName())) {
            return hasNext;
        }
        return method.invoke(proxied, args);
    }
}
