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

@Keyword("maximum")
public class MaximumErrorDigester extends AbstractErrorDigester {

   @Override
   public void digest(OFSErrors errors, String entity, JsonNode report)
   {
      JsonNode message = report.findValue("message");
      JsonNode exclusive = report.get("exclusiveMaximum");
      String code = (exclusive == null) ? "maximum_too_large" : "maximum_not_exclusive";
      String field = report.path("instance").get("pointer").asText();
      addArguments(report, reject(errors, code, entity, field, message.asText()));
   }

   public void addArguments(JsonNode report, OFSError error) {
      JsonNode value = report.get("maximum");
      JsonNode found = report.get("found");
      if(found == null) found = value;
      error.put("minimum", asText(value))
         .put("found", asText(found));

   }

}
