package com.ofs.server.errors;

import com.ofs.server.model.OFSErrors;
import org.springframework.http.HttpStatus;

public class ServerException extends OFSException {

   public ServerException(HttpStatus status)
   {
      super(status);
   }

   public ServerException(HttpStatus status, OFSErrors errors)
   {
      super(status, errors);
   }

}
