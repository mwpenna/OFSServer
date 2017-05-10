package com.ofs.server.form.error;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class DefaultErrorDigesterFactory implements ErrorDigesterFactory {

    private final Map<String,ErrorDigester> digesters = new LinkedHashMap<>();


    @Autowired
    public DefaultErrorDigesterFactory(List<ErrorDigester> components)
    {
        for(ErrorDigester digester : components) {
            Keyword keyword = digester.getClass().getAnnotation(Keyword.class);
            if(!StringUtils.isEmpty(keyword.value())) {
                digesters.put(keyword.value(), digester);
            }
        }
    }


    @Override
    public ErrorDigester create(String keyword)
    {
        return (keyword != null) ? digesters.get(keyword) : null;
    }
}
