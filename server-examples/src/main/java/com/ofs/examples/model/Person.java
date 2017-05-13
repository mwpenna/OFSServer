package com.ofs.examples.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ofs.examples.utils.StringUtils;
import com.ofs.server.model.BaseOFSEntity;
import lombok.Data;

import java.net.URI;
import java.util.UUID;

@Data
public class Person extends BaseOFSEntity {

    private String name;
    private UUID id;

    public Person() {

    }

    public Person (URI href) {
        super(href);
    }

    @JsonIgnore
    public String getIdFromHref() {
        return StringUtils.getIdFromURI(getHref());
    }
}
