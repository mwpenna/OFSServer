package com.ofs.server.page;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@Component
public class PageResolver implements HandlerMethodArgumentResolver {

    @Value("${MAX_LIMIT_OVERRIDE:500}")
    private int maxLimit = 500;


    @Override
    public boolean supportsParameter(MethodParameter parameter)
    {
        return parameter.getParameterType() == Page.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mvc, NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
            throws Exception
    {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        return Page.from(servletRequest, maxLimit);
    }
}
