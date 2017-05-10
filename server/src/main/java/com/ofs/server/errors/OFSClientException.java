package com.ofs.server.errors;

import com.ofs.server.model.OFSErrors;
import org.springframework.http.HttpStatus;

public class OFSClientException extends OFSException {

    public OFSClientException(HttpStatus httpStatus) {
        super(httpStatus);
    }

    public OFSClientException(HttpStatus status, OFSErrors errors) {
        super(status, errors);
    }
}
