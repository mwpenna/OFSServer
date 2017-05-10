package com.ofs.server.errors;

import com.ofs.server.model.OFSErrors;
import org.springframework.http.HttpStatus;

public class ServiceUnavailableException extends ServerException {

   private int retryAfterSeconds;

   public ServiceUnavailableException()
   {
      super(HttpStatus.SERVICE_UNAVAILABLE);
   }

   public ServiceUnavailableException(int retryAfterSeconds)
   {
      super(HttpStatus.SERVICE_UNAVAILABLE);
      this.retryAfterSeconds = retryAfterSeconds;
   }

   public ServiceUnavailableException(OFSErrors errors)
   {
      super(HttpStatus.SERVICE_UNAVAILABLE, errors);
   }

   public ServiceUnavailableException(OFSErrors errors, int retryAfterSeconds)
   {
      super(HttpStatus.SERVICE_UNAVAILABLE, errors);
      this.retryAfterSeconds = retryAfterSeconds;
   }

   public int getRetryAfter()
   {
      return retryAfterSeconds;
   }

}
