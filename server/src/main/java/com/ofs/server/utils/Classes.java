package com.ofs.server.utils;

public class Classes {
    public static ClassLoader resolveClassLoader()
    {
        return ClassLoaderResolver.getClassLoader(1);
    }

    /**
     * This non-instantiable non-subclassable class acts as the global point
     * for choosing a ClassLoader for dynamic class/resource loading at any
     * point in an application.
     */
    static abstract class ClassLoaderResolver {


        private static final int CALL_CONTEXT_OFFSET = 3; // may need to change if this class is redesigned

        private static ClassLoaderResolver.Resolver CALLER_RESOLVER;

        static {
            try {
                // this can fail if the current SecurityManager does not allow
                // RuntimePermission ("createSecurityManager"):

                CALLER_RESOLVER = new ClassLoaderResolver.CallerResolver();
            } catch (SecurityException se) {
                CALLER_RESOLVER = new ClassLoaderResolver.SimpleResolver();
            }
        }


        /**
         * This method selects the "best" classloader instance to be used for
         * class/resource loading by whoever calls this method. The decision
         * typically involves choosing between the caller's current, thread
         * context, system, and other classloaders in the JVM.
         *
         * @return classloader to be used by the caller
         *          ['null' indicates the primordial loader]
         */
        public static synchronized ClassLoader getClassLoader()
        {
            return getClassLoader(0);
        }


        static synchronized ClassLoader getClassLoader(int level)
        {
            final Class<?> caller = getCallerClass(level);

            final ClassLoader callerLoader = caller.getClassLoader();
            final ClassLoader contextLoader = Thread.currentThread ().getContextClassLoader ();

            ClassLoader result;

            // if 'callerLoader' and 'contextLoader' are in a parent-child
            // relationship, always choose the child:

            if (isChild (contextLoader, callerLoader)) {
                result = callerLoader;
            } else if (isChild (callerLoader, contextLoader)) {
                result = contextLoader;
            } else {
                // this else branch could be merged into the previous one,
                // but I show it here to emphasize the ambiguous case:
                result = contextLoader;
            }

            final ClassLoader systemLoader = ClassLoader.getSystemClassLoader ();

            // precaution for when deployed as a bootstrap or extension class:
            if (isChild (result, systemLoader))
                result = systemLoader;

            return result;
        }


        private ClassLoaderResolver()
        {
        }

        /*
         * Indexes into the current method call context with a given offset.
         */
        private static Class<?> getCallerClass(final int callerOffset)
        {
            return CALLER_RESOLVER.getClassContext(CALL_CONTEXT_OFFSET + callerOffset);
        }


        /**
         * Returns 'true' if 'loader2' is a delegation child of 'loader1' [or if
         * 'loader1'=='loader2']. Of course, this works only for classloaders that
         * set their parent pointers correctly. 'null' is interpreted as the
         * primordial loader [i.e., everybody's parent].
         */
        private static boolean isChild (final ClassLoader loader1, ClassLoader loader2)
        {
            if (loader1 == loader2) return true;
            if (loader2 == null) return false;
            if (loader1 == null) return true;

            for ( ; loader2 != null; loader2 = loader2.getParent ()) {
                if (loader2 == loader1) return true;
            }

            return false;
        }




        private static interface Resolver {
            Class<?> getClassContext(int level);
        }

        /**
         * A helper class to get the call context. It subclasses SecurityManager to
         * make getClassContext() accessible. An instance of CallerResolver only
         * needs to be created, not installed as an actual security manager.
         */
        private static final class CallerResolver extends SecurityManager implements ClassLoaderResolver.Resolver {
            public Class<?> getClassContext(int level)
            {
                return super.getClassContext()[level];
            }
        }

        private static final class SimpleResolver implements ClassLoaderResolver.Resolver {
            public Class<?> getClassContext(int level)
            {
                return ClassLoaderResolver.SimpleResolver.class;
            }
        }

    }
}
