package com.ofs.server.form.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.util.Annotations;
import com.ofs.server.form.error.RequestContext;
import com.ofs.server.form.update.UpdatingContext;
import com.ofs.server.model.OFSEntity;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ObjectSearch <T extends OFSEntity> {

    private final ObjectMapper jackson;
    private final JavaType type;

    private ObjectSearch(RequestContext context, JavaType javaType) {
        this.jackson = context.getMapper();
        this.type = javaType;
    }

    public List<T> search(List<T> resultList, JsonNode request) throws IOException {

        ObjectReader reader = jackson.readerFor(type);
        T searchValues = reader.readValue(request);

        BeanDescription desc = jackson.getDeserializationConfig().introspect(type);

        process(searchValues, desc, request);

        return resultList;
    }

    public static <T extends OFSEntity> ObjectSearch<T> createFor(RequestContext context, Class<T> cls)
    {
        JavaType javaType = context.getMapper().getTypeFactory().constructType(cls);
        if(!javaType.isConcrete()) throw new IllegalArgumentException("does not yet support non-concrete classes");
        return new ObjectSearch<T>(context, javaType);
    }

    private void process(T searchValues, BeanDescription desc, JsonNode node)
    {
        for(BeanPropertyDefinition propDef : filter(desc)) {
            AnnotatedMember accessor = propDef.getAccessor();
            Object fieldValue = accessor.getValue(searchValues);
            String doSomething = "";
        }
    }

    private List<BeanPropertyDefinition> filter(BeanDescription desc)
    {
        List<BeanPropertyDefinition> results = desc.findProperties();

        Annotations annotations = desc.getClassAnnotations();
        JsonIgnoreProperties ignore = annotations.get(JsonIgnoreProperties.class);
        if(ignore != null && !ignore.allowSetters()) {
            // Filter out explicitly ignored
            final Set<String> ignores = newHashSet(ignore.value());
            results = results.stream().filter(prop -> !ignores.contains(prop.getName())).collect(Collectors.toList());
        }

        return results;
    }

    private HashSet<String> newHashSet(String... items) {
        HashSet result = new HashSet();
        Collections.addAll(result, items);
        return result;
    }
}
