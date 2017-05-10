/**
 * Copyright 2016 XpertSoftware
 * <p>
 * Created By: cfloersch
 * Date: 5/8/2016
 */
package com.ofs.server.form.error;

import com.fasterxml.jackson.databind.JsonNode;
import com.ofs.server.model.OFSErrors;

public interface ErrorDigester {

   void digest(OFSErrors errors, String entity, JsonNode report);

}
