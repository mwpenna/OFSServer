/**
 * Copyright 2016 XpertSoftware
 * <p>
 * Created By: cfloersch
 * Date: 5/8/2016
 */
package com.ofs.server.form.error;

import com.fasterxml.jackson.databind.JsonNode;
import com.ofs.server.model.OFSErrors;

@Keyword("oneOf")
public class OneOfErrorDigester extends AbstractErrorDigester {

   @Override
   public void digest(OFSErrors errors, String entity, JsonNode report)
   {
      JsonNode message = report.findValue("message");
      String field = report.path("instance").get("pointer").asText();
      reject(errors, "subschema_one_of_mismatch", entity, field, message.asText());

      JsonNode subReports = report.findValue("reports");
      // TODO Parse through these subReports potentially delegating to the appropriate error digester
   }

}
