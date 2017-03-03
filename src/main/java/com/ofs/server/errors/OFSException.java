package com.ofs.server.errors;

import com.ofs.server.model.OFSErrors;
import org.springframework.http.HttpStatus;

public class OFSException extends RuntimeException {

    private OFSErrors errors;
    private HttpStatus httpStatus;

    public OFSException(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public OFSException(HttpStatus httpStatus, OFSErrors errors) {
        this(httpStatus);
        this.errors = errors;
    }

    public String getMessage() {
        return String.format("%s %s", httpStatus.value(), httpStatus.getReasonPhrase());
    }

    public HttpStatus getStatus() { return httpStatus; }

    public OFSErrors getErrors() {
        return errors;
    }
}
