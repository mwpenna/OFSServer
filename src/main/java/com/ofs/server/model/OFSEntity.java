package com.ofs.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.ZonedDateTime;

@JsonPropertyOrder("")
public interface OFSEntity extends OFSServerObject {

    @JsonProperty
    ZonedDateTime getCreatedOn();
}
