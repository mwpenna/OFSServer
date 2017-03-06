package com.ofs.server.errors;

import com.ofs.server.model.OFSErrors;
import org.springframework.http.HttpStatus;

import java.net.URI;

public class ConflictException extends OFSClientException {

   private URI resource;

   public ConflictException(URI resource)
   {
      super(HttpStatus.CONFLICT);
      this.resource = resource;
   }

   public ConflictException(URI resource, OFSErrors errors)
   {
      super(HttpStatus.CONFLICT, errors);
      this.resource = resource;
   }


   public URI getResource()
   {
      return resource;
   }

}
