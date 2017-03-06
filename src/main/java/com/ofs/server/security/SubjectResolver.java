package com.ofs.server.security;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class SubjectResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter)
    {
        return parameter.getParameterType() == Subject.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mvc, NativeWebRequest request, WebDataBinderFactory binder)
            throws Exception
    {
        return SecurityContext.getSubject();
    }
}
