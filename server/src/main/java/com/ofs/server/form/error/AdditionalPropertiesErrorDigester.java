/**
 * Copyright 2016 XpertSoftware
 * <p>
 * Created By: cfloersch
 * Date: 5/8/2016
 */
package com.ofs.server.form.error;

import com.fasterxml.jackson.databind.JsonNode;
import com.ofs.server.model.OFSErrors;
import com.ofs.server.model.OFSError;

@Keyword("additionalProperties")
public class AdditionalPropertiesErrorDigester extends AbstractErrorDigester {

   @Override
   public void digest(OFSErrors errors, String entity, JsonNode report)
   {
      JsonNode message = report.findValue("message");
      String field = report.path("instance").get("pointer").asText();
      OFSError error = reject(errors, "additional_properties_not_allowed", entity, field, message.asText());

      error.put("unwanted", report.get("unwanted").toString());
   }

}
