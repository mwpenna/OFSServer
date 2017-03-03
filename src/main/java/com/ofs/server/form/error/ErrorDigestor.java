package com.ofs.server.form.error;

import com.fasterxml.jackson.databind.JsonNode;
import com.ofs.server.model.OFSErrors;

public interface ErrorDigestor {

    void digest(OFSErrors errors, String entity, JsonNode report);

}
