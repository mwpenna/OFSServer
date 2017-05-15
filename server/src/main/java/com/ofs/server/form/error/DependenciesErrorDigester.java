/**
 * Copyright 2016 XpertSoftware
 * <p>
 * Created By: cfloersch
 * Date: 5/8/2016
 */
package com.ofs.server.form.error;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.ofs.server.model.OFSError;
import com.ofs.server.model.OFSErrors;

import static com.ofs.server.utils.Strings.isEmpty;
import static java.lang.String.format;

@Keyword("dependencies")
public class DependenciesErrorDigester extends AbstractErrorDigester {

   @Override
   public void digest(OFSErrors errors, String entity, JsonNode report)
   {
      JsonNode fields = report.findValue("missing");
      JsonNode property = report.findValue("property");
      if(fields instanceof ArrayNode) {
         for(JsonNode field : fields) {
            String fieldName = construct(report.path("instance").get("pointer").asText(), field.asText());
            String devMsg = format("%s is required when %s is defined", field.asText(), property.asText());
            addArguments(report, reject(errors, "required_dependency_missing", entity, fieldName, devMsg));
         }
      }
   }

   protected void addArguments(JsonNode report, OFSError error)
   {
      error.put("related", report.get("property").asText())
            .put("missing", report.get("missing").toString());
   }


   private String construct(String pointer, String field)
   {
      return (isEmpty(pointer)) ? field : format("%s/%s", pointer, field);
   }

}
