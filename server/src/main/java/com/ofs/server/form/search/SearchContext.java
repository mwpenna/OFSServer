package com.ofs.server.form.search;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;

import java.util.List;
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

    public static <T> SearchContext<T> create(T request, List<T> entityList) {
        return new SearchContext<>(request, entityList);
    }
}
