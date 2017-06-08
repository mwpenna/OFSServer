package com.ofs.server.errors;

import com.ofs.server.model.OFSErrors;
import org.springframework.http.HttpStatus;

public class ClientException extends OFSException {
    public ClientException(HttpStatus status)
    {
        super(status);
    }

    public ClientException(HttpStatus status, OFSErrors errors)
    {
        super(status, errors);
    }
}
