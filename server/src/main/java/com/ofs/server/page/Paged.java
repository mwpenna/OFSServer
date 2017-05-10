package com.ofs.server.page;

import com.ofs.server.utils.Classes;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.List;
import java.util.NavigableSet;
import java.util.RandomAccess;
import java.util.Set;
import java.util.SortedSet;

public interface Paged {

    boolean hasNext();

    @SuppressWarnings("unchecked")
    static <E> Collection<E> collection(Collection<? extends E> source, boolean hasNext)
    {
        ClassLoader loader = Classes.resolveClassLoader();
        Class[] interfaces = new Class[] {Collection.class, Paged.class};
        InvocationHandler handler = new PagedHandler(source, hasNext);
        return (Collection) Proxy.newProxyInstance(loader, interfaces, handler);
    }

    @SuppressWarnings("unchecked")
    static <E> Set<E> set(Set<? extends E> source, boolean hasNext)
    {
        ClassLoader loader = Classes.resolveClassLoader();
        Class[] interfaces = new Class[] {Set.class, Paged.class};
        InvocationHandler handler = new PagedHandler(source, hasNext);
        return (Set) Proxy.newProxyInstance(loader, interfaces, handler);
    }

    @SuppressWarnings("unchecked")
    static <E> SortedSet<E> sortedSet(SortedSet<? extends E> source, boolean hasNext)
    {
        ClassLoader loader = Classes.resolveClassLoader();
        Class[] interfaces = new Class[] {SortedSet.class, Paged.class};
        InvocationHandler handler = new PagedHandler(source, hasNext);
        return (SortedSet) Proxy.newProxyInstance(loader, interfaces, handler);
    }

    @SuppressWarnings("unchecked")
    static <E> NavigableSet<E> navigableSet(NavigableSet<? extends E> source, boolean hasNext)
    {
        ClassLoader loader = Classes.resolveClassLoader();
        Class[] interfaces = new Class[] {NavigableSet.class, Paged.class};
        InvocationHandler handler = new PagedHandler(source, hasNext);
        return (NavigableSet) Proxy.newProxyInstance(loader, interfaces, handler);
    }

    @SuppressWarnings("unchecked")
    static <E> List<E> list(List<? extends E> source, boolean hasNext)
    {
        ClassLoader loader = Classes.resolveClassLoader();
        Class[] interfaces = (source instanceof RandomAccess)
                ? new Class[] {List.class, Paged.class, RandomAccess.class}
                : new Class[] {List.class, Paged.class};
        InvocationHandler handler = new PagedHandler(source, hasNext);
        return (List) Proxy.newProxyInstance(loader, interfaces, handler);
    }
}
