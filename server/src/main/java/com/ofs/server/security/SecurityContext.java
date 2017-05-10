package com.ofs.server.security;

public class SecurityContext {
    private static final ThreadLocal<Subject> subjects = new ThreadLocal<>();

    public static Subject getSubject() { return subjects.get(); }

    static void bind(Subject subject)
    {
        subjects.set(subject);
    }

    static void clear()
    {
        subjects.remove();
    }
}
