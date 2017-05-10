package com.ofs.server.page;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class PageResolverTest {

    @Mock
    private NativeWebRequest webRequest;
    @Mock
    private HttpServletRequest servletRequest;

    @InjectMocks
    private PageResolver objectUnderTest;

    @Before
    public void setUp()
    {
        initMocks(this);
        when(webRequest.getNativeRequest(eq(HttpServletRequest.class))).thenReturn(servletRequest);
    }

    @Test
    public void whenStartAndLimitUndefined_thenPageHasZeroFiveHundredValues()
            throws Exception
    {
        when(servletRequest.getParameter(eq("start"))).thenReturn(null);
        when(servletRequest.getParameter(eq("limit"))).thenReturn(null);
        Page resposne = (Page) objectUnderTest.resolveArgument(null, null, webRequest, null);
        assertEquals(0, resposne.getStart());
        assertEquals(25, resposne.getLimit());
    }

    @Test
    public void whenStartUndefinedAndLimitZero_thenPageHasZeroTwentyFiveValues()
            throws Exception
    {
        when(servletRequest.getParameter(eq("start"))).thenReturn(null);
        when(servletRequest.getParameter(eq("limit"))).thenReturn("0");
        Page resposne = (Page) objectUnderTest.resolveArgument(null, null, webRequest, null);
        assertEquals(0, resposne.getStart());
        assertEquals(25, resposne.getLimit());
    }

    @Test
    public void whenStartUndefinedAndLimitOne_thenPageHasZeroOneValues()
            throws Exception
    {
        when(servletRequest.getParameter(eq("start"))).thenReturn(null);
        when(servletRequest.getParameter(eq("limit"))).thenReturn("1");
        Page resposne = (Page) objectUnderTest.resolveArgument(null, null, webRequest, null);
        assertEquals(0, resposne.getStart());
        assertEquals(1, resposne.getLimit());
    }

    @Test
    public void whenStartUndefinedAndLimitTwentyFive_thenPageHasZeroTwentyFiveValues()
            throws Exception
    {
        when(servletRequest.getParameter(eq("start"))).thenReturn(null);
        when(servletRequest.getParameter(eq("limit"))).thenReturn("25");
        Page resposne = (Page) objectUnderTest.resolveArgument(null, null, webRequest, null);
        assertEquals(0, resposne.getStart());
        assertEquals(25, resposne.getLimit());
    }

    @Test
    public void whenStartUndefinedAndLimitFiveHundredOne_thenPageHasZeroFiveHundredValues()
            throws Exception
    {
        when(servletRequest.getParameter(eq("start"))).thenReturn(null);
        when(servletRequest.getParameter(eq("limit"))).thenReturn("501");
        Page resposne = (Page) objectUnderTest.resolveArgument(null, null, webRequest, null);
        assertEquals(0, resposne.getStart());
        assertEquals(500, resposne.getLimit());
    }

    @Test
    public void whenStartOneHundredAndLimitFiveHundredOne_thenPageHasOneHundredFiveHundredValues()
            throws Exception
    {
        when(servletRequest.getParameter(eq("start"))).thenReturn("100");
        when(servletRequest.getParameter(eq("limit"))).thenReturn("501");
        Page resposne = (Page) objectUnderTest.resolveArgument(null, null, webRequest, null);
        assertEquals(100, resposne.getStart());
        assertEquals(500, resposne.getLimit());
    }

    @Test
    public void whenStartOneHundredAndLimitUndefined_thenPageHasOneHundredTwentyFiveValues()
            throws Exception
    {
        when(servletRequest.getParameter(eq("start"))).thenReturn("100");
        when(servletRequest.getParameter(eq("limit"))).thenReturn(null);
        Page resposne = (Page) objectUnderTest.resolveArgument(null, null, webRequest, null);
        assertEquals(100, resposne.getStart());
        assertEquals(25, resposne.getLimit());
    }

    @Test
    public void whenStartIsNegativeLimitIsUndefined_thenPageHasZeroTwentyFiveValues()
            throws Exception
    {
        when(servletRequest.getParameter(eq("start"))).thenReturn("-1");
        when(servletRequest.getParameter(eq("limit"))).thenReturn(null);
        Page resposne = (Page) objectUnderTest.resolveArgument(null, null, webRequest, null);
        assertEquals(0, resposne.getStart());
        assertEquals(25, resposne.getLimit());
    }

    @Test
    public void whenLimitIsNegativeStartIsUndefined_thenPageHasZeroTwentyFiveValues()
            throws Exception
    {
        when(servletRequest.getParameter(eq("start"))).thenReturn(null);
        when(servletRequest.getParameter(eq("limit"))).thenReturn("-1");
        Page resposne = (Page) objectUnderTest.resolveArgument(null, null, webRequest, null);
        assertEquals(0, resposne.getStart());
        assertEquals(25, resposne.getLimit());
    }

    @Test
    public void whenLimitIsNotANumberStartIsUndefined_thenPageHasZeroTwentyFiveValues()
            throws Exception
    {
        when(servletRequest.getParameter(eq("start"))).thenReturn(null);
        when(servletRequest.getParameter(eq("limit"))).thenReturn("hello");
        Page resposne = (Page) objectUnderTest.resolveArgument(null, null, webRequest, null);
        assertEquals(0, resposne.getStart());
        assertEquals(25, resposne.getLimit());
    }

    @Test
    public void whenStartIsNotANumberLimitIsUndefined_thenPageHasZeroTwentyFiveValues()
            throws Exception
    {
        when(servletRequest.getParameter(eq("start"))).thenReturn("hello");
        when(servletRequest.getParameter(eq("limit"))).thenReturn(null);
        Page resposne = (Page) objectUnderTest.resolveArgument(null, null, webRequest, null);
        assertEquals(0, resposne.getStart());
        assertEquals(25, resposne.getLimit());
    }
}