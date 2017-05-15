/**
 * Copyright 2016 XpertSoftware
 * <p>
 * Created By: cfloersch
 * Date: 5/8/2016
 */
package com.ofs.server.form.error;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.ofs.server.model.OFSErrors;

import static com.ofs.server.utils.Strings.isEmpty;
import static java.lang.String.format;

@Keyword("required")
public class RequiredErrorDigester extends AbstractErrorDigester {

   @Override
   public void digest(OFSErrors errors, String entity, JsonNode report)
   {
      JsonNode fields = report.findValue("missing");
      if(fields instanceof ArrayNode) {
         for(JsonNode field : fields) {
            String ptr = report.path("instance").get("pointer").asText();
            String devMsg = format("Missing required field %s", field.asText());
            reject(errors, "required_field_missing", entity, construct(ptr, field.asText()), devMsg);
         }
      }
   }

   private String construct(String pointer, String field)
   {
      return (isEmpty(pointer)) ? field : format("%s/%s", pointer, field);
   }
}
