package com.ofs.server.form;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.ofs.server.errors.BadRequestException;
import com.ofs.server.form.error.ErrorDigester;
import com.ofs.server.form.error.RequestContext;
import com.ofs.server.form.schema.JsonSchema;
import com.ofs.server.form.update.ChangeSet;
import com.ofs.server.form.update.ObjectUpdater;
import com.ofs.server.model.OFSEntity;
import com.ofs.server.model.OFSErrors;
import org.springframework.http.HttpHeaders;
import org.xpertss.json.util.Strings;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static java.lang.String.format;

public class OFSServerForm<T extends OFSEntity> {
    private final DocumentContext documentContext;
    private final RequestContext context;
    private final JsonNode requestBody;
    private final Class<T> type;

    OFSServerForm(RequestContext context, JsonNode requestBody, Class<T> type)
    {
        Configuration conf = Configuration.builder()
                .jsonProvider(new JacksonJsonNodeJsonProvider())
                .build();

        this.documentContext = JsonPath.using(conf).parse(requestBody);
        this.requestBody = requestBody;
        this.context = context;
        this.type = type;
    }

    public T create(Object id) throws IOException{
        T entity;

        try {
            Constructor<T> constructor = type.getConstructor(id.getClass());
            entity = constructor.newInstance(id);
        } catch(NoSuchMethodException e) {
            throw new IllegalArgumentException(format("%s is missing a uri constructor", type.getName()), e);
        } catch(IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(format("Failed to create entity %s", type.getName()), e);
        }

        JsonSchema schema = context.getSchema();
        if(schema != null) {
            ProcessingReport report = schema.validateUnchecked(requestBody, true);
            if(!report.isSuccess()) {
                throw new BadRequestException(createErrors(report, context.getEntityName()));
            }
        }

        ObjectMapper mapper = context.getMapper();
        ObjectReader reader = mapper.readerForUpdating(entity);
        return type.cast(reader.readValue(requestBody));
    }

    public ChangeSet update(T entity)
            throws BadRequestException, IOException
    {
        // Check CAS headers
        HttpHeaders headers = context.getRequestBody().getHeaders();

        // Schema Validation if it exists
        JsonSchema schema = context.getSchema();
        if(schema != null) {
            ProcessingReport report = schema.validateUnchecked(requestBody, true);
            if(!report.isSuccess()) {
                throw new BadRequestException(createErrors(report, context.getEntityName()));
            }
        }

        ObjectUpdater<T> updater = ObjectUpdater.createFor(context, type);
        return updater.update(entity, requestBody);
    }


    public <V> V findProperty(String jsonPath)
    {
        try {
            return documentContext.read(jsonPath);
        } catch(PathNotFoundException e) {
            return null;
        }
    }

    private OFSErrors createErrors(ProcessingReport report, String entityName)
    {
        OFSErrors errors = new OFSErrors();
        for(ProcessingMessage msg : report) {
            JsonNode error = msg.asJson();
            String keyword = error.path("keyword").asText();
            if(Strings.isEmpty(keyword))
                throw new RuntimeException("Possible schema syntax issue found", msg.asException());
            ErrorDigester digestor = context.getFactory().create(keyword);
            if(digestor != null) {
                digestor.digest(errors, entityName, error);
            } else {
                throw new RuntimeException(format("No digester found for %s", keyword), msg.asException());
            }
        }
        return errors;
    }

}
