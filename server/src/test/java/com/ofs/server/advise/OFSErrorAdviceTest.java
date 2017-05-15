package com.ofs.server.advise;

import com.ofs.server.utils.Strings;
import org.junit.Test;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static org.junit.Assert.*;


public class OFSErrorAdviceTest {

    @Test
    public void testResource()
    {
        ResourceBundle bundle = ResourceBundle.getBundle("OFSErrors", Locale.US);
        assertEquals("Full", getResource(bundle, "user.company.href.required_field_missing"));
        assertEquals("Property", getResource(bundle, "user.href.required_field_missing"));
        assertEquals("Partial", getResource(bundle, "href.required_field_missing"));
        assertEquals("Minimal", getResource(bundle, "required_field_missing"));
    }

    @Test(expected = MissingResourceException.class)
    public void testMissingResource()
    {
        ResourceBundle bundle = ResourceBundle.getBundle("OFSErrors", Locale.US);
        getResource(bundle, "user.company.href.not");
    }

    private String getResource(ResourceBundle bundle, String code)
    {
        String[] parts = code.split("\\.");
        for(int i = 0; i < parts.length; i++) {
            try {
                String key = Strings.join(".", parts, i, parts.length - i);
                return bundle.getString(key);
            } catch(MissingResourceException e) { /* Silently ignore */ }
        }
        throw new MissingResourceException(
                String.format("Can't find resource %s for bundle %s", code, bundle.getClass().getName()),
                bundle.getClass().getName(), code);
    }
}