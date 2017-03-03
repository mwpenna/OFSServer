package com.ofs.server.form.schema;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jackson.jsonpointer.JsonPointerException;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.load.RefResolver;
import com.github.fge.jsonschema.core.load.SchemaLoader;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.core.processing.CachingProcessor;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.processing.ProcessorMap;
import com.github.fge.jsonschema.core.ref.JsonRef;
import com.github.fge.jsonschema.core.report.ListReportProvider;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ReportProvider;
import com.github.fge.jsonschema.library.Library;
import com.github.fge.jsonschema.messages.JsonSchemaConfigurationBundle;
import com.github.fge.jsonschema.processors.data.SchemaContext;
import com.github.fge.jsonschema.processors.data.ValidatorList;
import com.github.fge.jsonschema.processors.syntax.SyntaxValidator;
import com.github.fge.jsonschema.processors.validation.SchemaContextEquivalence;
import com.github.fge.jsonschema.processors.validation.ValidationChain;
import com.github.fge.jsonschema.processors.validation.ValidationProcessor;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.base.Function;

import java.util.Map;

public class SchemaFactory {

    private static final MessageBundle BUNDLE
            = MessageBundles.getBundle(JsonSchemaConfigurationBundle.class);
    private static final MessageBundle CORE_BUNDLE
            = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private static final Function<SchemaContext, JsonRef> FUNCTION = input -> input.getSchema().getDollarSchema();

    /*
     * Elements provided by the builder
     */
    final ReportProvider reportProvider;
    final LoadingConfiguration loadingCfg;
    final ValidationConfiguration validationCfg;

    /*
     * Generated elements
     */
    private final SchemaLoader loader;
    private final JsonValidator validator;
    private final SyntaxValidator syntaxValidator;


    public SchemaFactory()
    {
        reportProvider = new ListReportProvider(LogLevel.INFO, LogLevel.FATAL);
        loadingCfg = LoadingConfiguration.byDefault();
        validationCfg = ValidationConfiguration.byDefault();

        loader = new SchemaLoader(loadingCfg);
        Processor<SchemaContext, ValidatorList> processor = buildProcessor();
        validator = new JsonValidator(loader, new ValidationProcessor(validationCfg, processor), reportProvider);
        syntaxValidator = new SyntaxValidator(validationCfg);
    }

    /**
     * Return the main schema/instance validator provided by this factory
     *
     * @return a {@link JsonValidator}
     */
    public JsonValidator getValidator()
    {
        return validator;
    }

    /**
     * Return the syntax validator provided by this factory
     *
     * @return a {@link SyntaxValidator}
     */
    public SyntaxValidator getSyntaxValidator()
    {
        return syntaxValidator;
    }





    /**
     * Build an instance validator tied to a schema
     *
     * <p>Note that the validity of the schema is <b>not</b> checked. Use {@link
     * #getSyntaxValidator()} if you are not sure.</p>
     *
     * @param schema the schema
     * @return a {@link JsonSchema}
     * @throws ProcessingException schema is a {@link MissingNode}
     * @throws NullPointerException schema is null
     */
    public JsonSchema getJsonSchema(final JsonNode schema)
            throws ProcessingException
    {
        BUNDLE.checkNotNull(schema, "nullSchema");
        return validator.buildJsonSchema(schema, JsonPointer.empty());
    }

    /**
     * Build an instance validator tied to a subschema from a main schema
     *
     * <p>Note that the validity of the schema is <b>not</b> checked. Use {@link
     * #getSyntaxValidator()} if you are not sure.</p>
     *
     * @param schema the schema
     * @param ptr a JSON Pointer as a string
     * @return a {@link JsonSchema}
     * @throws ProcessingException {@code ptr} is not a valid JSON Pointer, or
     * resolving the pointer against the schema leads to a {@link MissingNode}
     * @throws NullPointerException schema is null, or pointer is null
     */
    public JsonSchema getJsonSchema(final JsonNode schema, final String ptr)
            throws ProcessingException
    {
        BUNDLE.checkNotNull(schema, "nullSchema");
        CORE_BUNDLE.checkNotNull(ptr, "nullPointer");
        final JsonPointer pointer;
        try {
            pointer = new JsonPointer(ptr);
            return validator.buildJsonSchema(schema, pointer);
        } catch (JsonPointerException ignored) {
            // Cannot happen
        }
        throw new IllegalStateException("How did I get there??");
    }

    /**
     * Build an instance validator out of a schema loaded from a URI
     *
     * @param uri the URI
     * @return a {@link JsonSchema}
     * @throws ProcessingException failed to load from this URI
     * @throws NullPointerException URI is null
     */
    public JsonSchema getJsonSchema(final String uri)
            throws ProcessingException
    {
        CORE_BUNDLE.checkNotNull(uri, "nullURI");
        return validator.buildJsonSchema(uri);
    }



    private Processor<SchemaContext, ValidatorList> buildProcessor()
    {
        RefResolver resolver = new RefResolver(loader);

        Map<JsonRef, Library> libraries = validationCfg.getLibraries();
        Library defaultLibrary = validationCfg.getDefaultLibrary();
        ValidationChain defaultChain = new ValidationChain(resolver, defaultLibrary, validationCfg);
        ProcessorMap<JsonRef, SchemaContext, ValidatorList> map = new ProcessorMap<>(FUNCTION);
        map.setDefaultProcessor(defaultChain);

        for (Map.Entry<JsonRef, Library> entry: libraries.entrySet()) {
            ValidationChain chain = new ValidationChain(resolver, entry.getValue(), validationCfg);
            map.addEntry(entry.getKey(), chain);
        }

        Processor<SchemaContext, ValidatorList> processor = map.getProcessor();
        return new CachingProcessor<>(processor, SchemaContextEquivalence.getInstance());
    }
}
