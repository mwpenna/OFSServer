package com.ofs.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ofs.server.OFSServerId;
import com.ofs.server.client.AuthenticationClient;
import com.ofs.server.form.OFSServerFormResolver;
import com.ofs.server.security.AuthInterceptor;
import com.ofs.server.security.SubjectResolver;
import com.ofs.server.utils.Links;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static org.springframework.util.StringUtils.isEmpty;

@Configuration
@EnableWebMvc
public class OFSServerConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private OFSServerFormResolver formResolver;

    @Autowired
    private SubjectResolver subjectResolver;

    @Autowired
    @Qualifier("ofsObjectMapper")
    private ObjectMapper ofsObjectMapper;

    @Autowired
    private ApplicationContext context;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers)
    {
        argumentResolvers.add(0, new CookbookIdResolver());
        argumentResolvers.add(0, formResolver);
        argumentResolvers.add(0, subjectResolver);
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers)
    {
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters)
    {
        converters.add(new OFSMessageConverter(ofsObjectMapper));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor((AuthInterceptor) context.getBean("authInterceptor"));
    }

    private class OFSMessageConverter extends MappingJackson2HttpMessageConverter {

        private Pattern REGEX = Pattern.compile("^vnd\\.(manheim\\.v[0-9]+)\\+([a-z]+)$");;

        OFSMessageConverter(ObjectMapper mapper)
        {
            super(mapper);
        }

        protected void addDefaultHeaders(HttpHeaders headers, Object t, MediaType contentType)
                throws IOException
        {
            if (headers.getContentType() == null) {
                MediaType contentTypeToUse = contentType;
                if (contentType == null || contentType.isWildcardType() || contentType.isWildcardSubtype()) {
                    contentTypeToUse = getDefaultContentType(t);
                }
                else if (MediaType.APPLICATION_OCTET_STREAM.equals(contentType)) {
                    MediaType mediaType = getDefaultContentType(t);
                    contentTypeToUse = (mediaType != null ? mediaType : contentTypeToUse);
                }
                if (contentTypeToUse != null) {
                    Matcher matcher = REGEX.matcher(contentTypeToUse.getSubtype());
                    if(matcher.find()) {
                        headers.set("X-Manheim-Media-Type",
                                format("%s; format=%s", matcher.group(1), matcher.group(2)));
                        contentTypeToUse = getDefaultContentType(t);
                    }

                    headers.setContentType(contentTypeToUse);
                }
            }
            if (headers.getContentLength() < 0) {
                Long contentLength = getContentLength(t, headers.getContentType());
                if (contentLength != null) {
                    headers.setContentLength(contentLength);
                }
            }
        }

        @Override
        protected MediaType getDefaultContentType(Object object)
                throws IOException
        {
            if (object instanceof MappingJacksonValue) {
                object = ((MappingJacksonValue) object).getValue();
            }
            return MediaType.APPLICATION_JSON;
        }


    }


    private class CookbookIdResolver implements HandlerMethodArgumentResolver {

        @Override
        public boolean supportsParameter(MethodParameter parameter)
        {
            return (parameter.hasParameterAnnotation(OFSServerId.class));
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mvc, NativeWebRequest webRequest, WebDataBinderFactory binder)
                throws Exception
        {
            OFSServerId annotation = parameter.getParameterAnnotation(OFSServerId.class);
            if(parameter.getParameterType() != URI.class)
                throw new IllegalArgumentException("CookbookId parameters must be URIs");
            HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
            String path = (isEmpty(annotation.value())) ? request.getRequestURI() : annotation.value();
            return Links.generateIdUri(request, path, UUID.randomUUID());
        }

    }
}
