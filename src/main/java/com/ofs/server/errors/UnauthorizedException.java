/**
 * Copyright 2016 XpertSoftware
 * <p>
 * Created By: cfloersch
 * Date: 6/20/2016
 */
package com.ofs.server.errors;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends OFSClientException {

   private String scheme;
   private String realm;

   public UnauthorizedException(String scheme, String realm)
   {
      super(HttpStatus.UNAUTHORIZED);
      this.scheme = scheme;
      this.realm = realm;
   }

   public String getScheme()
   {
      return scheme;
   }

   public String getRealm()
   {
      return realm;
   }

}
