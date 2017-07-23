package com.ofs.server.form.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.util.Annotations;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SearchContext<T> {
    private T request;
    private List<T> entityList;

    private SearchContext(T request, List<T> entityList){
        this.request = request;
        this.entityList = entityList;
    }

    public T getRequest() {
        return this.request;
    }

    public List<T> getEntityList() {
        return this.entityList;
    }

    public void filterList(String searchKey, AnnotatedMember accessor) {
        this.entityList = this.getEntityList().stream().filter(
                entity -> accessor.getValue(entity).toString().toLowerCase().contains(searchKey.toLowerCase())).collect(Collectors.toList());
    }

    public void filterSubList(String searchKey, AnnotatedMember accessor, List searchList) {
        ObjectMapper mapper = new ObjectMapper();
        List<T> filteredList = new ArrayList();
        for(Object searchObject : searchList) {
            Map searchMap = (Map) searchObject;

            for(Object entity : entityList) {
                boolean meetSearchCriteria = false;
                List searchableList = (List) accessor.getValue(entity);
                for(Object object : searchableList) {
                    Map map = mapper.convertValue(object, Map.class);

                    for(Object key : searchMap.keySet()) {
                        if(map.containsKey(key)) {
                            String mapValue = (String) map.get(key);
                            String searchMapValue = (String) searchMap.get(key);

                            if(mapValue.toLowerCase().contains(searchMapValue.toLowerCase())) {
                                meetSearchCriteria = true;
                            }
                        }
                    }
                }

                if(meetSearchCriteria) {
                    filteredList.add((T) entity);
                }
            }
        }

        this.entityList = filteredList;
    }

    public static <T> SearchContext<T> create(T request, List<T> entityList) {
        return new SearchContext<>(request, entityList);
    }
}
