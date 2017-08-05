package com.ofs.examples.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.ofs.examples.utils.StringUtils;
import com.ofs.server.model.BaseOFSEntity;
import lombok.Data;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class Family extends BaseOFSEntity {
    private String name;
    private UUID id;
    private List<Person> familyMembers;
    private double numFamMembers;

    public Family() {

    }

    public Family(URI href) {
        super(href);
    }

    public Family(Map map) {
        this.setName((String) map.get("name"));

        ArrayList<Person> persons = new ArrayList<>();

        for(Object personMapObject : (List) map.get("familyMembers")) {
            Person person = new Person();
            Map personMap = (Map) personMapObject;
            person.setName((String) personMap.get("name"));
            persons.add(person);
        }

        this.setNumFamMembers(Double.parseDouble(map.get("numFamMembers").toString()));

        this.familyMembers = persons;
    }

    @JsonIgnore
    public String getIdFromHref() {
        return StringUtils.getIdFromURI(getHref());
    }
}
