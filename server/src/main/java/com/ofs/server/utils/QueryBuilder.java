package com.ofs.server.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class QueryBuilder {
    private List<String> params = new ArrayList();

    private QueryBuilder() {
    }

    public QueryBuilder clear() {
        this.params.clear();
        return this;
    }

    public int size() {
        return this.params.size();
    }

    public String[] get(String name) {
        ArrayList result = new ArrayList();
        if(name != null) {
            Iterator i$ = this.params.iterator();

            while(i$.hasNext()) {
                String param = (String)i$.next();
                if(param.startsWith(name + "=")) {
                    String[] parts = param.split("=");
                    if(parts.length > 2) {
                        result.add(Strings.join("=", parts, 1, parts.length - 1));
                    } else if(parts.length == 2) {
                        result.add(parts[1]);
                    } else if(parts.length == 1) {
                        result.add("");
                    }
                }
            }
        }

        return (String[])result.toArray(new String[result.size()]);
    }

    public QueryBuilder remove(String name) {
        if(name != null) {
            Iterator it = this.params.iterator();

            while(it.hasNext()) {
                String param = (String)it.next();
                if(param.startsWith(name + "=")) {
                    it.remove();
                }
            }
        }

        return this;
    }

    public QueryBuilder add(String name, String value) {
        this.params.add(Strings.notEmpty(name, "name can\'t be empty") + "=" + Strings.emptyIfNull(value));
        return this;
    }

    public QueryBuilder set(String name, String value) {
        name = Strings.notEmpty(name, "name can\'t be empty");
        Iterator it = this.params.iterator();

        while(it.hasNext()) {
            String param = (String)it.next();
            if(param.startsWith(name + "=")) {
                it.remove();
            }
        }

        this.params.add(name + "=" + Strings.emptyIfNull(value));
        return this;
    }

    public QueryBuilder setAll(String... nameValuePairs) {
        if(nameValuePairs.length % 2 != 0) {
            throw new IllegalArgumentException("uneven input arguments");
        } else {
            this.params.clear();

            for(int i = 0; i < nameValuePairs.length; i += 2) {
                this.add(nameValuePairs[i], nameValuePairs[i + 1]);
            }

            return this;
        }
    }

    public QueryBuilder setQuery(String query) {
        this.params.clear();
        String[] arr$ = Strings.emptyIfNull(query).split("&");
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            String pair = arr$[i$];
            if(!Strings.isEmpty(pair)) {
                this.params.add(pair);
            }
        }

        return this;
    }

    public String build() {
        StringBuilder buf = new StringBuilder();

        String pair;
        for(Iterator i$ = this.params.iterator(); i$.hasNext(); buf.append(pair)) {
            pair = (String)i$.next();
            if(buf.length() > 0) {
                buf.append("&");
            }
        }

        return buf.toString();
    }

    public static QueryBuilder create() {
        return new QueryBuilder();
    }

    public static QueryBuilder create(String query) {
        return (new QueryBuilder()).setQuery(query);
    }
}
