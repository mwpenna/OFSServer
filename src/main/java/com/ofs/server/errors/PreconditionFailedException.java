/**
 * Copyright 2016 XpertSoftware
 * <p/>
 * Created By: cfloersch
 * Date: 4/30/2016
 */
package com.ofs.server.errors;

import org.springframework.http.HttpStatus;

public class PreconditionFailedException extends OFSClientException {
   public PreconditionFailedException()
   {
      super(HttpStatus.PRECONDITION_FAILED);
   }
}
