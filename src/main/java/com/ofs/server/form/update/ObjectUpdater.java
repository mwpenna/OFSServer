package com.ofs.server.form.update;

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
import com.ofs.server.model.OFSEntity;
import xpertss.lang.Objects;
import xpertss.util.Lists;
import xpertss.util.Sets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ObjectUpdater <T extends OFSEntity> {

    private final ObjectMapper jackson;
    private final JavaType type;

    private ObjectUpdater(RequestContext context, JavaType javaType) {
        this.jackson = context.getMapper();
        this.type = javaType;
    }

    public ChangeSet update(T oldEntity, JsonNode request) throws IOException
    {
        ObjectReader reader = jackson.readerFor(type);
        T newEntity = reader.readValue(request);

        UpdatingContext<T> context = UpdatingContext.create(oldEntity, newEntity);

        // Now get a list a properties for the java entity that might have been deserialized
        // based on accessors/visibility/ignore/etc
        BeanDescription desc = jackson.getDeserializationConfig().introspect(type);
        process(context, desc, request);
        return context.changes();
    }

    private void process(UpdatingContext context, BeanDescription desc, JsonNode node)
    {
        for(BeanPropertyDefinition propDef : filter(desc)) {
            if(propDef.couldDeserialize()) {
                String propName = propDef.getName();
                JsonNode value = node.get(propName);
                if(value != null) {
                    if(value.isObject()) {
                        processObject(context, propDef, propName, value);
                    } else {
                        processField(context, propDef, propName);
                    }
                }
            }
        }
    }

    private void processObject(UpdatingContext context, BeanPropertyDefinition propDef, String propName, JsonNode node)
    {
        // NOTE: newValue cannot be null here??? If it was null it'd be in processField not processObject
        AnnotatedMember accessor = propDef.getAccessor();
        Object oldValue = accessor.getValue(context.getOldEntity());
        Object newValue = accessor.getValue(context.getNewEntity());
        if(oldValue == null) {
            AnnotatedMember mutator = propDef.getMutator();
            mutator.setValue(context.getOldEntity(), newValue);
            context.addChange(propName, oldValue, newValue);
        } else {
            UpdatingContext childContext = context.newFor(propName, oldValue, newValue);
            JavaType javaType = jackson.getTypeFactory().constructType(newValue.getClass());
            BeanDescription desc = jackson.getDeserializationConfig().introspect(javaType);
            process(childContext, desc, node);
        }
    }

    private void processField(UpdatingContext context, BeanPropertyDefinition propDef, String propName)
    {
        AnnotatedMember accessor = propDef.getAccessor();
        Object oldValue = accessor.getValue(context.getOldEntity());
        Object newValue = accessor.getValue(context.getNewEntity());
        if(!equal(oldValue, newValue)) {
            AnnotatedMember mutator = propDef.getMutator();
            mutator.setValue(context.getOldEntity(), newValue);
            context.addChange(propName, oldValue, newValue);
        }
    }

    private boolean equal(Object oldValue, Object newValue)
    {
        if(oldValue instanceof Number && oldValue instanceof Comparable) {
            if(newValue == null) {
                return false;
            }
            return ((Comparable) oldValue).compareTo(newValue) == 0;
        }
        return Objects.equal(oldValue, newValue);
    }

    private List<BeanPropertyDefinition> filter(BeanDescription desc)
    {
        List<BeanPropertyDefinition> results = new ArrayList<>();

        Annotations annotations = desc.getClassAnnotations();
        JsonIgnoreProperties ignore = annotations.get(JsonIgnoreProperties.class);
        if(ignore != null && !ignore.allowSetters()) {
            // Filter out explicitly ignored
            final Set<String> ignores = Sets.newHashSet(ignore.value());
            results = Lists.filter(results, prop -> !ignores.contains(prop.getName()));
        }
        // TODO Need to filter out JsonIgnoreType
        return results;
    }

    public static <T extends OFSEntity> ObjectUpdater<T> createFor(RequestContext context, Class<T> cls)
    {
        JavaType javaType = context.getMapper().getTypeFactory().constructType(cls);
        if(!javaType.isConcrete()) throw new IllegalArgumentException("does not yet support non-concrete classes");
        return new ObjectUpdater<T>(context, javaType);
    }
}
