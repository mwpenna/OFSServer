package com.ofs.server.form;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.google.common.collect.ImmutableList;
import com.ofs.server.OFSController;
import com.ofs.server.errors.BadRequestException;
import com.ofs.server.form.error.ErrorDigesterFactory;
import com.ofs.server.form.error.RequestContext;
import com.ofs.server.form.schema.JsonSchema;
import com.ofs.server.form.schema.SchemaFactory;
import com.ofs.server.model.OFSEntity;
import com.ofs.server.model.OFSErrors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import xpertss.lang.CaseFormat;
import xpertss.lang.Strings;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.*;
import static xpertss.lang.CaseFormat.*;
import static xpertss.lang.CaseFormat.UPPER_CAMEL;
import static xpertss.lang.Objects.ifNull;

@Component
public class OFSServerFormResolver implements HandlerMethodArgumentResolver {
    private static final List<MediaType> SUPPORTED = ImmutableList.of(
            MediaType.APPLICATION_JSON_UTF8,
            new MediaType("application", "*+json", UTF_8));


    private final ConcurrentMap<String,JsonSchema> schemaCache = new ConcurrentHashMap<>();
    private final SchemaLoader LOADER = new SchemaLoader();

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ErrorDigesterFactory factory;

    @Autowired
    @Qualifier("ofsObjectMapper")
    private ObjectMapper cookbookObjectMapper;


    @Override
    public boolean supportsParameter(MethodParameter parameter)
    {
        return parameter.getParameterType() == OFSServerForm.class;
    }


    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mvc, NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
            throws Exception
    {
        Class<? extends OFSEntity> entityClass = findEntityClass(parameter.getGenericParameterType());

        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        ServletServerHttpRequest requestBody = new ServletServerHttpRequest(servletRequest);

        checkContentType(requestBody.getHeaders());

        String entityName = entityName(servletRequest.getRequestURI());

        JsonSchema jsonSchema = findSchema(parameter);
        if(jsonSchema != null && !Strings.isEmpty(jsonSchema.getTitle())) {
            entityName = jsonSchema.getTitle();
        } else {
            CaseFormat format = ifNull(forString(entityName), UPPER_CAMEL);
            entityName = format.to(LOWER_UNDERSCORE, entityName);
        }


        try (Reader input = new InputStreamReader(requestBody.getBody(), UTF_8)) {
            JsonNode entity = cookbookObjectMapper.readTree(input);

            RequestContext context = createRequestContext(parameter);
            context.setEntityName(entityName);
            context.setRequestBody(requestBody);
            context.setSchema(jsonSchema);

            return new OFSServerForm<>(context, entity, entityClass);
        } catch(JsonMappingException | JsonParseException e) {
            OFSErrors errors = new OFSErrors();
            errors.rejectValue(format("%s.json_entity_malformed", entityName), e.getOriginalMessage());
            throw new BadRequestException(errors);
        }

    }

    private void checkContentType(HttpHeaders headers)
            throws HttpMediaTypeNotSupportedException
    {
        try {
            MediaType contentType = headers.getContentType();
            if(contentType == null) contentType = MediaType.APPLICATION_OCTET_STREAM;
            for(MediaType supportedMediaType : SUPPORTED) {
                if(supportedMediaType.includes(contentType)) return;
            }
            throw new HttpMediaTypeNotSupportedException(contentType, SUPPORTED);
        } catch (InvalidMediaTypeException e) {
            throw new HttpMediaTypeNotSupportedException(e.getMessage());
        }
    }


    private Class<? extends OFSEntity> findEntityClass(Type genericType)
    {
        if(genericType instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) genericType;
            Type rawType = type.getRawType();
            if(rawType == OFSServerForm.class) {
                Type[] typeArgs = type.getActualTypeArguments();
                if(typeArgs.length == 1 && typeArgs[0] instanceof Class) {
                    Class targetType = (Class) typeArgs[0];
                    if(OFSEntity.class.isAssignableFrom(targetType)) {
                        //noinspection unchecked
                        return (Class<OFSEntity>) targetType;
                    }
                }
            }
        }
        throw new IllegalArgumentException("CookbookForm's must define generic type");
    }

    private JsonSchema findSchema(MethodParameter parameter)
    {
        ValidationSchema schema = parameter.getMethodAnnotation(ValidationSchema.class);
        return (schema != null)  ? schemaCache.computeIfAbsent(schema.value(), LOADER) : null;
    }

    private static String entityName(String input)
    {
        String[] parts = input.split("/");
        String entityName = (parts.length > 1) ? parts[1] : parts[0];
        if(entityName.endsWith("s")) entityName = entityName.substring(0, entityName.length() - 1);
        return entityName;
    }

    private RequestContext createRequestContext(MethodParameter parameter)
    {
        RequestContext context = new RequestContext(factory, cookbookObjectMapper);
        OFSController annotation = parameter.getContainingClass().getAnnotation(OFSController.class);
//        if(!Strings.isEmpty(annotation.resolver())) {
//            context.setResolver(applicationContext.getBean(annotation.resolver(), ViewResolver.class));
//        } else {
//            context.setResolver(new DefaultViewResolver());
//        }
//        context.setFilter(annotation.filter());
        return context;
    }


    private static class SchemaLoader implements Function<String,JsonSchema> {

        private final SchemaFactory factory;

        private SchemaLoader()
        {
            factory = new SchemaFactory();
        }

        @Override
        public JsonSchema apply(String url)
        {
            JsonNode rawSchema;
            try {
                try {
                    rawSchema = JsonLoader.fromURL(new URL(url));
                } catch(MalformedURLException e) {
                    if(!url.startsWith("/")) url = format("/%s", url);
                    rawSchema = JsonLoader.fromResource(url);
                }
                return factory.getJsonSchema(rawSchema);
            } catch(ProcessingException e) {
                throw new IllegalArgumentException(format("%s is not a valid JSON schema", url));
            } catch(IOException e) {
                throw new IllegalArgumentException(format("%s could not be retrieved", url));
            }
        }
    }
}
