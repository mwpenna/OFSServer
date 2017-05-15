package com.ofs.server.utils;

import java.util.ArrayList;

public enum CaseFormat {
    LOWER_HYPHEN {
        String create(String[] parts) {
            StringBuilder builder = new StringBuilder();
            String[] arr$ = parts;
            int len$ = parts.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String part = arr$[i$];
                if (builder.length() > 0) {
                    builder.append("-");
                }

                builder.append(part);
            }

            return builder.toString();
        }

        boolean matches(String str) {
            boolean foundHyphen = false;

            for (int i = 0; i < str.length(); ++i) {
                char c = str.charAt(i);
                if (Character.isUpperCase(c)) {
                    return false;
                }

                if (c == 95) {
                    return false;
                }

                if (c == 45) {
                    foundHyphen = true;
                }
            }

            return foundHyphen;
        }

        String[] parts(String str) {
            return str.toLowerCase().split("-");
        }
    },
    LOWER_UNDERSCORE {
        String create(String[] parts) {
            StringBuilder builder = new StringBuilder();
            String[] arr$ = parts;
            int len$ = parts.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String part = arr$[i$];
                if (builder.length() > 0) {
                    builder.append("_");
                }

                builder.append(part);
            }

            return builder.toString();
        }

        boolean matches(String str) {
            boolean foundUnderscore = false;

            for (int i = 0; i < str.length(); ++i) {
                char c = str.charAt(i);
                if (Character.isUpperCase(c)) {
                    return false;
                }

                if (c == 45) {
                    return false;
                }

                if (c == 95) {
                    foundUnderscore = true;
                }
            }

            return foundUnderscore;
        }

        String[] parts(String str) {
            return str.toLowerCase().split("_");
        }
    },
    LOWER_CAMEL {
        String create(String[] parts) {
            StringBuilder builder = new StringBuilder();
            String[] arr$ = parts;
            int len$ = parts.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String part = arr$[i$];
                if (builder.length() > 0) {
                    builder.append(Strings.firstCharToUpper(part));
                } else {
                    builder.append(part);
                }
            }

            return builder.toString();
        }

        boolean matches(String str) {
            boolean firstIsLower = false;
            boolean foundUpper = false;

            for (int i = 0; i < str.length(); ++i) {
                char c = str.charAt(i);
                if (i == 0 && Character.isLowerCase(c)) {
                    firstIsLower = true;
                }

                if (Character.isUpperCase(c)) {
                    foundUpper = true;
                }

                if (c == 45) {
                    return false;
                }

                if (c == 95) {
                    return false;
                }
            }

            return firstIsLower && foundUpper;
        }
    },
    UPPER_CAMEL {
        String create(String[] parts) {
            StringBuilder builder = new StringBuilder();
            String[] arr$ = parts;
            int len$ = parts.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String part = arr$[i$];
                builder.append(Strings.firstCharToUpper(part));
            }

            return builder.toString();
        }

        boolean matches(String str) {
            boolean firstIsUpper = false;
            boolean foundUpper = false;
            boolean foundLower = false;

            for (int i = 0; i < str.length(); ++i) {
                char c = str.charAt(i);
                if (i == 0 && Character.isUpperCase(c)) {
                    firstIsUpper = true;
                }

                if (Character.isUpperCase(c)) {
                    foundUpper = true;
                }

                if (Character.isLowerCase(c)) {
                    foundLower = true;
                }

                if (c == 45) {
                    return false;
                }

                if (c == 95) {
                    return false;
                }
            }

            return firstIsUpper && foundUpper && foundLower;
        }
    },
    UPPER_UNDERSCORE {
        String create(String[] parts) {
            StringBuilder builder = new StringBuilder();
            String[] arr$ = parts;
            int len$ = parts.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String part = arr$[i$];
                if (builder.length() > 0) {
                    builder.append("_");
                }

                builder.append(part.toUpperCase());
            }

            return builder.toString();
        }

        boolean matches(String str) {
            boolean foundUnderscore = false;

            for (int i = 0; i < str.length(); ++i) {
                char c = str.charAt(i);
                if (Character.isLowerCase(c)) {
                    return false;
                }

                if (c == 45) {
                    return false;
                }

                if (c == 95) {
                    foundUnderscore = true;
                }
            }

            return foundUnderscore;
        }

        String[] parts(String str) {
            return str.toLowerCase().split("_");
        }
    };

    private CaseFormat() {
    }

    public final String to(CaseFormat format, String str) {
        return format == this ? str : format.create(this.parts(str));
    }

    public static CaseFormat forString(String str) {
        CaseFormat[] arr$ = values();
        int len$ = arr$.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            CaseFormat format = arr$[i$];
            if (format.matches(str)) {
                return format;
            }
        }

        return null;
    }

    String create(String[] parts) {
        return null;
    }

    boolean matches(String str) {
        return false;
    }

    String[] parts(String str) {
        StringBuilder builder = new StringBuilder();
        ArrayList parts = new ArrayList();

        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                parts.add(builder.toString().toLowerCase());
                builder.setLength(0);
            }

            builder.append(c);
        }

        parts.add(builder.toString().toLowerCase());
        return (String[]) parts.toArray(new String[parts.size()]);
    }
}
