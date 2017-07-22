package com.ofs.server.form.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
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
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ObjectSearch <T extends OFSEntity> {

    private final ObjectMapper jackson;
    private final JavaType type;
    private final RequestContext context;
    private JsonNode request;

    private ObjectSearch(RequestContext context, JavaType javaType) {
        this.jackson = context.getMapper();
        this.type = javaType;
        this.context = context;
    }

    public List<T> search(List<T> resultList, JsonNode request) throws IOException {

        this.request = request;
        ObjectReader reader = jackson.readerFor(type);
        T searchValues = reader.readValue(request);

        BeanDescription desc = jackson.getDeserializationConfig().introspect(type);
        SearchContext searchContext = SearchContext.create(searchValues, resultList);
        process(searchContext, desc, request);

        return searchContext.getEntityList();
    }

    public static <T extends OFSEntity> ObjectSearch<T> createFor(RequestContext context, Class<T> cls)
    {
        JavaType javaType = context.getMapper().getTypeFactory().constructType(cls);
        if(!javaType.isConcrete()) throw new IllegalArgumentException("does not yet support non-concrete classes");
        return new ObjectSearch<T>(context, javaType);
    }

    private void process(SearchContext searchContext, BeanDescription desc, JsonNode node) throws IOException {
        List<T> filteredList = null;
        for(BeanPropertyDefinition propDef : filter(desc)) {
            AnnotatedMember accessor = propDef.getAccessor();
            Object fieldValue = accessor.getValue(searchContext.getRequest());
            if(fieldValue != null) {
                if(List.class.isAssignableFrom(fieldValue.getClass())) {
                    TypeReference<List> mapType = new TypeReference<List>() {};
                    List searchList = jackson.readValue(node.get(propDef.getName()).toString(), mapType);
                    searchContext.filterSubList(fieldValue.toString(), accessor, searchList);
                }
                else {
                    searchContext.filterList(fieldValue.toString(), accessor);
                }
            }
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
