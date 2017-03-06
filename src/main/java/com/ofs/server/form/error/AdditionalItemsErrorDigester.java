package com.ofs.server.form.error;

import com.fasterxml.jackson.databind.JsonNode;
import com.ofs.server.model.OFSError;
import com.ofs.server.model.OFSErrors;

@Keyword("additionalItems")
public class AdditionalItemsErrorDigester extends AbstractErrorDigester {

   @Override
   public void digest(OFSErrors errors, String entity, JsonNode report)
   {
      JsonNode message = report.findValue("message");
      String field = report.path("instance").get("pointer").asText();
      addArguments(report, reject(errors, "additional_items_not_allowed", entity, field, message.asText()));
   }

   protected void addArguments(JsonNode report, OFSError error)
   {
      error.put("allowed", asText(report.get("allowed")));
      error.put("found", asText(report.get("found")));
   }

}
