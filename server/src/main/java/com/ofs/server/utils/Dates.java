package com.ofs.server.utils;

import org.springframework.http.HttpHeaders;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public final class Dates {
    private Dates() { }

    /**
     * Creates and returns a new {@link ZonedDateTime} built around the {@code UTC}
     * timezone.
     */
    public static ZonedDateTime now()
    {
        return ZonedDateTime.now(ZoneId.of("UTC"));
    }


    /**
     * Convert a {@link Date} to a {@link ZonedDateTime} ensuring to utilize {@code UTC}
     * timezone on the returned date.
     */
    public static ZonedDateTime toDateTime(Date date)
    {
        return (date != null) ? toDateTime(Instant.ofEpochMilli(date.getTime())) : null;
    }

    /**
     * Convert an {@link Instant} to a {@link ZonedDateTime} ensuring to utilize {@code UTC}
     * timezone on the returned date.
     */
    public static ZonedDateTime toDateTime(Instant instant)
    {
        return (instant != null) ? ZonedDateTime.ofInstant(instant, ZoneId.of("UTC")) : null;
    }

    /**
     * Convert a number of millis since epoch to a {@link ZonedDateTime} ensuring to utilize
     * {@code UTC} timezone on the returned date.
     */
    public static ZonedDateTime toDateTime(long epochMillis)
    {
        return toDateTime(Instant.ofEpochMilli(epochMillis));
    }






    /**
     * Convert a {@link ZonedDateTime} to a {@link Date}. If {@code null} is supplied then
     * {@code null} is returned. This is useful when converting to store in a database for
     * example.
     */
    public static Date toDate(ZonedDateTime zdt)
    {
        return (zdt != null) ? toDate(zdt.toInstant()) : null;
    }

    /**
     * Convert an {@link Instant} to a {@link Date}. If {@code null} is supplied then {@code null}
     * is returned. This is useful when converting to store in a database for example.
     */
    public static Date toDate(Instant instant)
    {
        return (instant != null) ? toDate(instant.toEpochMilli()) : null;
    }

    /**
     * Convert a number of millis since epoch to a {@link Date}. This is useful when converting to
     * store in a database for example.
     */
    public static Date toDate(long epochMillis)
    {
        return new Date(epochMillis);
    }




    /**
     * Parse the specified header into a {@link Date} object. Returns {@code null} if the specified
     * header does not exist.
     */
    public static Date asDate(HttpHeaders headers, String header)
    {
        long headerDate = headers.getFirstDate(header);
        return (headerDate >= 0) ? new Date(headerDate) : null;
    }

    /**
     * Parse the specified header into a {@link ZonedDateTime} object. Returns {@code null} if the
     * specified header does not exist.
     */
    public static ZonedDateTime asZonedDate(HttpHeaders headers, String header)
    {
        Instant headerDate = asInstant(headers, header);
        return (headerDate != null) ? ZonedDateTime.ofInstant(headerDate, ZoneId.of("UTC")) : null;
    }

    /**
     * Parse the specified header into an {@link Instant} object. Returns {@code null} if the
     * specified header does not exist.
     */
    public static Instant asInstant(HttpHeaders headers, String header)
    {
        long headerDate = headers.getFirstDate(header);
        return (headerDate >= 0) ? Instant.ofEpochMilli(headerDate) : null;
    }


    /**
     * Set the specified header to the given {@link Date} utilizing the HTTP specifications date
     * formatting.
     */
    public static void setDate(HttpHeaders headers, String name, Date date)
    {
        headers.setDate(name, date.getTime());
    }

    /**
     * Set the specified header to the given {@link ZonedDateTime} utilizing the HTTP specifications
     * date formatting.
     */
    public static void setDate(HttpHeaders headers, String name, ZonedDateTime date)
    {
        setDate(headers, name, date.toInstant());
    }

    /**
     * Set the specified header to the given {@link Instant} utilizing the HTTP specifications date
     * formatting.
     */
    public static void setDate(HttpHeaders headers, String name, Instant date)
    {
        headers.setDate(name, date.toEpochMilli());
    }
}
