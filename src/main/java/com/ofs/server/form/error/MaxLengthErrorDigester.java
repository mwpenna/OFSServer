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

@Keyword("maxLength")
public class MaxLengthErrorDigester extends AbstractErrorDigester {

   @Override
   public void digest(OFSErrors errors, String entity, JsonNode report)
   {
      JsonNode message = report.findValue("message");
      String field = report.path("instance").get("pointer").asText();
      addArguments(report, reject(errors, "maximum_length_exceeded", entity, field, message.asText()));
   }

   protected void addArguments(JsonNode report, OFSError error)
   {
      error.put("maximum", asText(report.get("maxLength")))
               .put("found", asText(report.get("found")))
               .put("value", asText(report.get("value")));
   }
}
