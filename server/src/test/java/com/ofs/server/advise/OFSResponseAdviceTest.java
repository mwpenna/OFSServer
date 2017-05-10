package com.ofs.server.advise;

import org.junit.Test;
import org.springframework.http.CacheControl;

import static org.junit.Assert.*;

public class OFSResponseAdviceTest {

    @Test
    public void testPublicCacheHeader()
    {
        CacheControl cache = CacheControl.empty().mustRevalidate();
        cache = cache.cachePublic();
        assertEquals("must-revalidate, public", cache.getHeaderValue());
    }

    @Test
    public void testPrivateCacheHeader()
    {
        CacheControl cache = CacheControl.empty().mustRevalidate();
        cache = cache.cachePrivate();
        assertEquals("must-revalidate, private", cache.getHeaderValue());
    }
}