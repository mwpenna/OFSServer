package com.ofs.server.advise;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.ofs.server.OFSController;
import com.ofs.server.OFSServerId;
import com.ofs.server.errors.NotFoundException;
import com.ofs.server.model.OFSEntity;
import com.ofs.server.security.SecurityContext;
import com.ofs.server.security.Subject;
import com.ofs.server.utils.Links;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import xpertss.lang.Objects;
import xpertss.lang.Strings;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static com.fasterxml.jackson.databind.DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS;
import static com.ofs.server.utils.Links.normalizePath;
import static java.lang.String.format;

@ControllerAdvice(annotations = OFSController.class)
public class OFSResponseAdvice implements ResponseBodyAdvice<Object>{

    @Autowired
    private ApplicationContext context;

    @Autowired
    @Qualifier("ofsObjectMapper")
    private ObjectMapper ofsObjectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converters)
    {
        if(returnType.getMethodAnnotation(ResponseBody.class) != null) {
            Type genType = returnType.getGenericParameterType();
            if(genType instanceof Class) {
                Class cls = (Class) genType;
                Class compare = (cls.isArray()) ? cls.getComponentType() : cls;
                return OFSEntity.class.isAssignableFrom(compare);
            } else if(genType instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genType;
                Class<?> c = (Class<?>) pt.getRawType();
                if(Collection.class.isAssignableFrom(c)) {
                    Type[] genArgs = pt.getActualTypeArguments();
                    if(genArgs.length == 1 && genArgs[0] instanceof Class) {
                        return OFSEntity.class.isAssignableFrom((Class)genArgs[0]);
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> converter,
                                  ServerHttpRequest input, ServerHttpResponse output)
    {
        OFSController annotation = returnType.getDeclaringClass().getAnnotation(OFSController.class);
        RequestContext context = RequestContext.of(input, output);

        Type genType = returnType.getGenericParameterType();
        if(isEntity(genType)) {
            OFSEntity entity = (OFSEntity) body;
            ObjectNode object = encodeEntity(context, entity);
            if(object == null) throw new NotFoundException();

            body = processObject(context, body, object);
            HttpHeaders headers = context.getResponseHeaders();
        } else {
            Collection items;
            if(body != null) {
                if(genType instanceof ParameterizedType) {
                    items = (Collection) body;
                } else {
                    items = Arrays.asList((Object[]) body);
                }
            } else {
                items = Collections.emptySet();
            }
            body = processCollection(context, items);
        }

        processCacheControl(context);

        return body;
    }

    private void processCacheControl(RequestContext context)
    {
        HttpHeaders headers = context.getResponseHeaders();
        if(headers.getCacheControl() == null) {
            CacheControl cache = CacheControl.noCache();
            if(Objects.isOneOf(context.getRequest().getMethod(), "GET", "HEAD")) {
                cache = CacheControl.empty().mustRevalidate();
                cache = cache.cachePrivate();

            }
            headers.setCacheControl(cache.getHeaderValue());
        }

    }

    private JsonNode processCollection(RequestContext context, Collection items)
    {
        HttpServletRequest request = context.getRequest();
        ObjectNode result = ofsObjectMapper.createObjectNode();
        if(Objects.isOneOf(request.getMethod(), "GET", "HEAD")) {
            URI requestUri = generateRequestUri(request);
            result.put("href", requestUri.toString());
        }
        ArrayNode array = result.put("count", items.size())
                .putArray("items");

        for(Object obj : items) {
            if(obj != null) {
                OFSEntity entity = (OFSEntity) obj;
                ObjectNode object = encodeEntity(context, entity);
                if(object != null) array.add(processObject(context, obj, object));
            }
        }

        result.put("count", array.size());

        return result;
    }






    private JsonNode processObject(RequestContext context, Object body, ObjectNode object)
    {
        JavaType javaType = ofsObjectMapper.getTypeFactory().constructType(body.getClass());
        BeanDescription desc = ofsObjectMapper.getDeserializationConfig().introspect(javaType);
        JsonNode idNode = object.findValue("href");
        if(idNode != null && !isWebUri(idNode.asText())) {
            BeanPropertyDefinition propDef = findPropertyDefinition(desc, "href");
            AnnotatedMember accessor = propDef.getAccessor();
            OFSServerId annotation = accessor.getAnnotation(OFSServerId.class);
            if(annotation != null && !StringUtils.isEmpty(annotation.value())) {
                URI uri = Links.generateIdUri(context.getRequest(),
                        annotation.value(),
                        accessor.getValue(body));
                object.replace("href", object.textNode(uri.toString()));
            }
        }

        // Rearrange getCreatedOn/getUpdatedOn to end of object
        JsonNode createdOn = object.remove("createdOn");
        if(createdOn != null) object.set("createdOn", createdOn);
        JsonNode updatedOn = object.remove("updatedOn");
        if(updatedOn != null) object.set("updatedOn", updatedOn);

        return object;
    }




    @SuppressWarnings("unchecked")
    private <T extends JsonNode> T encodeEntity(RequestContext context, OFSEntity entity)
            throws IllegalArgumentException
    {
        if(entity == null) return null;

        Subject subject = SecurityContext.getSubject();

        ObjectWriter writer = ofsObjectMapper.writer();
        TokenBuffer buf = new TokenBuffer(ofsObjectMapper, false);
        if (ofsObjectMapper.isEnabled(USE_BIG_DECIMAL_FOR_FLOATS)) {
            buf = buf.forceUseOfBigDecimal(true);
        }
        JsonNode result;
        try {
            writer.writeValue(buf, entity);
            JsonParser jp = buf.asParser();
            result = ofsObjectMapper.readTree(jp);
            jp.close();
        } catch (IOException e) { // should not occur, no real i/o...
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return (T) result;
    }

    private BeanPropertyDefinition findPropertyDefinition(BeanDescription desc, String propName)
    {
        for(BeanPropertyDefinition propDef : desc.findProperties()) {
            if(Objects.equal(propName, propDef.getName())) return propDef;
        }
        throw new IllegalArgumentException(format("%s does not define a CookbookId", desc.getBeanClass().getName()));
    }

    private boolean isWebUri(String str)
    {
        try {
            URI uri = new URI(str);
            return Objects.isOneOf(uri.getScheme(), "http", "https");
        } catch(URISyntaxException e) {
            return false;
        }
    }



    private boolean isEntity(Type genType)
    {
        return (genType instanceof Class && !((Class)genType).isArray());
    }


    private URI generateRequestUri(HttpServletRequest request)
    {
        String authority = Links.generateAuthority(request);
        String path = normalizePath(request.getRequestURI());
        return URI.create(format("%s%s", authority, path));
    }

    private static class RequestContext {

        private HttpServletRequest request;
        private HttpHeaders responseHeaders;

        public HttpServletRequest getRequest()
        {
            return request;
        }

        public HttpHeaders getResponseHeaders()
        {
            return responseHeaders;
        }

        public static RequestContext of(ServerHttpRequest input, ServerHttpResponse output)
        {
            RequestContext context = new RequestContext();
            context.request = ((ServletServerHttpRequest) input).getServletRequest();
            context.responseHeaders = output.getHeaders();
            return context;
        }
    }

}
