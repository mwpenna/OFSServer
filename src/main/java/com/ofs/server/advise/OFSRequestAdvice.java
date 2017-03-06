package com.ofs.server.advise;

import com.ofs.server.OFSController;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Type;

@ControllerAdvice(annotations = OFSController.class)
public class OFSRequestAdvice implements RequestBodyAdvice {

    @Override
    public boolean supports(MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType)
    {
        return false;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage input, MethodParameter parameter, Type targetType,
                                  Class<? extends HttpMessageConverter<?>> converterType)
    {
        return null;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage input, MethodParameter parameter, Type targetType,
                                           Class<? extends HttpMessageConverter<?>> converterType)
            throws IOException
    {
        return null;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage input, MethodParameter parameter, Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType)
    {
        return null;
    }
}
