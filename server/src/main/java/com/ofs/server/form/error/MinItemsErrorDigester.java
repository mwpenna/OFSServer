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

@Keyword("minItems")
public class MinItemsErrorDigester extends AbstractErrorDigester {

   @Override
   public void digest(OFSErrors errors, String entity, JsonNode report)
   {
      JsonNode message = report.findValue("message");
      String field = report.path("instance").get("pointer").asText();
      addArguments(report, reject(errors, "array_too_small", entity, field, message.asText()));
   }

   protected void addArguments(JsonNode report, OFSError error)
   {
      error.put("minimum", asText(report.get("minItems")))
            .put("found", asText(report.get("found")));
   }

}
