package com.ofs.server.errors;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends OFSClientException {

   public ForbiddenException()
   {
      super(HttpStatus.FORBIDDEN);
   }

}
