/**
 * Copyright 2016 XpertSoftware
 * <p>
 * Created By: cfloersch
 * Date: 5/8/2016
 */
package com.ofs.server.form.error;

import com.fasterxml.jackson.databind.JsonNode;
import com.ofs.server.model.OFSError;
import com.ofs.server.model.OFSErrors;

@Keyword("maxProperties")
public class MaxPropertiesErrorDigester extends AbstractErrorDigester {

   @Override
   public void digest(OFSErrors errors, String entity, JsonNode report)
   {
      JsonNode message = report.findValue("message");
      String field = report.path("instance").get("pointer").asText();
      OFSError error = reject(errors, "too_many_properties_found", entity, field, message.asText());

      error.put("required", asText(report.get("required")))
            .put("found", asText(report.get("found")));
   }

}
