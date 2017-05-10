package com.ofs.server.errors;

import com.ofs.server.model.OFSErrors;
import org.springframework.http.HttpStatus;

public class BadRequestException extends OFSClientException {

    public BadRequestException(OFSErrors errors) {
        super(HttpStatus.BAD_REQUEST, errors);
    }
}
