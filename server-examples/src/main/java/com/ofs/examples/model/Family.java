package com.ofs.examples.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ofs.examples.utils.StringUtils;
import com.ofs.server.model.BaseOFSEntity;
import lombok.Data;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Data
public class Family extends BaseOFSEntity {
    private String name;
    private UUID id;
    private List<Person> familyMembers;

    public Family() {

    }

    public Family(URI href) {
        super(href);
    }

    @JsonIgnore
    public String getIdFromHref() {
        return StringUtils.getIdFromURI(getHref());
    }
}
