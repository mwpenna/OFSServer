package com.ofs.server.page;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.RandomAccess;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PagedTest {

    @Test
    public void testPagedList()
    {
        List<String> source = new ArrayList<>();
        source.add("Chris");
        source.add("Kevin");
        List<String> proxy = Paged.list(source, true);
        assertTrue(proxy instanceof List);
        assertTrue(proxy instanceof RandomAccess);
        assertTrue(proxy instanceof Paged);
        assertFalse(proxy instanceof ArrayList);
        assertTrue(((Paged)proxy).hasNext());
        assertEquals(source, proxy);
        assertEquals(source.hashCode(), proxy.hashCode());
        assertEquals(source.toString(), proxy.toString());
        assertEquals(source.size(), proxy.size());
        assertEquals(source.get(0), proxy.get(0));
        assertEquals(source.get(1), proxy.get(1));
    }

    @Test
    public void testPagedSet()
    {
        Set<String> source = new LinkedHashSet<>();
        source.add("Chris");
        source.add("Kevin");
        Set<String> proxy = Paged.set(source, true);
        assertTrue(proxy instanceof Set);
        assertTrue(proxy instanceof Paged);
        assertFalse(proxy instanceof LinkedHashSet);
        assertTrue(((Paged)proxy).hasNext());
        assertEquals(source, proxy);
        assertEquals(source.hashCode(), proxy.hashCode());
        assertEquals(source.toString(), proxy.toString());
        assertEquals(source.size(), proxy.size());
        Iterator<String> it = proxy.iterator();
        assertTrue(it.hasNext());
        assertEquals("Chris", it.next());
        assertTrue(it.hasNext());
        assertEquals("Kevin", it.next());
    }

    @Test
    public void testPagedSortedSet()
    {
        SortedSet<String> source = new TreeSet<>();
        source.add("Chris");
        source.add("Kevin");
        Set<String> proxy = Paged.sortedSet(source, true);
        assertTrue(proxy instanceof SortedSet);
        assertTrue(proxy instanceof Paged);
        assertFalse(proxy instanceof TreeSet);
        assertTrue(((Paged)proxy).hasNext());
        assertEquals(source, proxy);
        assertEquals(source.hashCode(), proxy.hashCode());
        assertEquals(source.toString(), proxy.toString());
        assertEquals(source.size(), proxy.size());
        Iterator<String> it = proxy.iterator();
        assertTrue(it.hasNext());
        assertEquals("Chris", it.next());
        assertTrue(it.hasNext());
        assertEquals("Kevin", it.next());
    }
}