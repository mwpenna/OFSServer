package com.ofs.server.errors;

import org.springframework.http.HttpStatus;

public class NotFoundException extends OFSClientException {

   public NotFoundException()
   {
      super(HttpStatus.NOT_FOUND);
   }

}
