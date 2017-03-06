/**
 * Copyright 2016 XpertSoftware
 * <p/>
 * Created By: cfloersch
 * Date: 4/30/2016
 */
package com.ofs.server.errors;

import org.springframework.http.HttpStatus;

public class UnsupportedMediaTypeException extends OFSClientException {
   public UnsupportedMediaTypeException()
   {
      super(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
   }
}
