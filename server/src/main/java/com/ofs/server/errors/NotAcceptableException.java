package com.ofs.server.errors;

import org.springframework.http.HttpStatus;

public class NotAcceptableException extends ClientException {

    public NotAcceptableException() {
        super(HttpStatus.NOT_ACCEPTABLE);
    }
}
