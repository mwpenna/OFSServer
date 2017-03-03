package com.ofs.server.model;

import com.ofs.server.utils.Dates;

import java.net.URI;
import java.time.ZonedDateTime;

public class BaseOFSEntity implements OFSEntity {

    private URI href;
    private ZonedDateTime createdOn;

    public BaseOFSEntity() {}

    public BaseOFSEntity(URI href) {
        this.href = href;
        this.createdOn = Dates.now();
    }

    public URI getHref() {
        return href;
    }

    public void setHref(URI href) {
        this.href = href;
    }

    @Override
    public ZonedDateTime getCreatedOn() {
        return null;
    }

    public void setCreatedOn(ZonedDateTime createdOn) {
        this.createdOn = createdOn;
    }
}
