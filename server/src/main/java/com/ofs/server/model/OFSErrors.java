package com.ofs.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class OFSErrors implements Iterable<OFSError> {
    @JsonProperty
    private Set<OFSError> errors = new LinkedHashSet<>();

    public OFSErrors() {
    }


    @JsonIgnore
    public List<OFSError> getErrors() {
        return new ArrayList<>(errors);
    }

    @JsonIgnore
    public List<OFSError> getGlobalErrors() {
        return errors.stream().filter(e -> e.getProperty() == null)
                .collect(Collectors.toList());
    }

    @JsonIgnore
    public List<OFSError> getFieldErrors() {
        return errors.stream().filter(e -> e.getProperty() != null)
                .collect(Collectors.toList());
    }


    @JsonIgnore
    public OFSError getFieldError(String field) {
        return errors.stream().filter(e -> field != null && Objects.equals(e.getProperty(), field))
                .findFirst().orElse(null);
    }

    @JsonIgnore
    public List<OFSError> getFieldErrors(String field) {
        return errors.stream().filter(e -> field != null && Objects.equals(e.getProperty(), field))
                .collect(Collectors.toList());
    }

    public OFSError rejectValue(String errorCode, String developerMessage) {
        OFSError error = new OFSError(errorCode, developerMessage);
        errors.add(error);
        return error;
    }

    public OFSError rejectValue(String errorCode, String field, String developerMessage) {
        OFSError error = new OFSError(errorCode, field, developerMessage);
        errors.add(error);
        return error;
    }

    @JsonIgnore
    public int size() {
        return errors.size();
    }

    @JsonIgnore
    public boolean isEmpty() {
        return errors.isEmpty();
    }


    @Override
    public Iterator<OFSError> iterator() {
        return errors.iterator();
    }

    public String toString() {
        return errors.toString();
    }
}
