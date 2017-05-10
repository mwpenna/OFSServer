package com.ofs.server.form.error;

import com.fasterxml.jackson.databind.JsonNode;
import com.ofs.server.model.OFSError;
import com.ofs.server.model.OFSErrors;

@Keyword("format")
public class FormatErrorDigester extends AbstractErrorDigester{
    @Override
    public void digest(OFSErrors errors, String entity, JsonNode report) {
        JsonNode message = report.findValue("message");
        String field = report.path("instance").get("pointer").asText();
        addArguments(report, reject(errors, "invalid_format", entity, field, message.asText()));
    }

    protected void addArguments(JsonNode report, OFSError error)
    {
        error.put("format", report.get("attribute").asText())
                .put("value", report.get("value").asText());
    }
}
