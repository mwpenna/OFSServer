package com.ofs.server.form.schema;


import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.processing.ProcessingResult;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.ref.JsonRef;
import com.github.fge.jsonschema.core.report.ListProcessingReport;
import com.github.fge.jsonschema.core.report.MessageProvider;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.report.ReportProvider;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.core.tree.SimpleJsonTree;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.processors.validation.ValidationProcessor;

import javax.annotation.concurrent.Immutable;

/**
 * Single-schema instance validator
 *
 * <p>This is the class you will use the most often. It is, in essence, a {@link
 * JsonValidator} initialized with a single JSON Schema. Note however that this
 * class still retains the ability to resolve JSON References.</p>
 *
 * <p>It has no public constructors: you should use the appropriate methods in
 * {@link SchemaFactory} to obtain an instance of this class.</p>
 */
@Immutable
public final class JsonSchema {

    private final ValidationProcessor processor;
    private final SchemaTree schema;
    private final ReportProvider reportProvider;

    /**
     * Package private constructor
     *
     * @param processor the validation processor
     * @param schema the schema to bind to this instance
     * @param reportProvider the report provider
     */
    JsonSchema(ValidationProcessor processor, SchemaTree schema, ReportProvider reportProvider)
    {
        this.processor = processor;
        this.schema = schema;
        this.reportProvider = reportProvider;
    }




    public JsonRef getDollarSchema()
    {
        return schema.getDollarSchema();
    }

    public String getTitle()
    {
        JsonNode node = schema.getBaseNode().findValue("title");
        return (node != null) ? node.asText() : null;
    }

    public String getDescription()
    {
        JsonNode node = schema.getBaseNode().findValue("description");
        return (node != null) ? node.asText() : null;
    }



    /**
     * Validate an instance and return a processing report
     *
     * @param instance the instance to validate
     * @param deepCheck validate children even if container (array, object) is
     * invalid
     * @return a processing report
     * @throws ProcessingException a processing error occurred during validation
     *
     * @see JsonValidator#validate(JsonNode, JsonNode, boolean)
     *
     * @since 2.1.8
     */
    public ProcessingReport validate(final JsonNode instance, final boolean deepCheck)
            throws ProcessingException
    {
        return doValidate(instance, deepCheck);
    }

    /**
     * Validate an instance and return a processing report
     *
     * <p>This calls {@link #validate(JsonNode, boolean)} with {@code false} as
     * a third argument.</p>
     *
     * @param instance the instance to validate
     * @return a processing report
     * @throws ProcessingException a processing error occurred during validation
     */
    public ProcessingReport validate(final JsonNode instance)
            throws ProcessingException
    {
        return validate(instance, false);
    }

    /**
     * Validate an instance and return a processing report (unchecked version)
     *
     * <p>Unchecked validation means that conditions which would normally cause
     * the processing to stop with an exception are instead inserted into the
     * resulting report.</p>
     *
     * <p><b>Warning</b>: this means that anomalous events like an unresolvable
     * JSON Reference, or an invalid schema, are <b>masked</b>!</p>
     *
     * @param instance the instance to validate
     * @param deepCheck validate children even if container (array, object) is
     * invalid
     * @return a report (a {@link ListProcessingReport} if an exception was
     * thrown during processing)
     *
     *
     * @see ProcessingResult#uncheckedResult(Processor, ProcessingReport,
     * MessageProvider)
     * @see JsonValidator#validate(JsonNode, JsonNode, boolean)
     *
     * @since 2.1.8
     */
    public ProcessingReport validateUnchecked(final JsonNode instance, final boolean deepCheck)
    {
        return doValidateUnchecked(instance, deepCheck);
    }

    /**
     * Validate an instance and return a processing report (unchecked version)
     *
     * <p>This calls {@link #validateUnchecked(JsonNode, boolean)} with {@code
     * false} as a third argument.</p>
     *
     * @param instance the instance to validate
     * @return a report (a {@link ListProcessingReport} if an exception was
     * thrown during processing)
     */
    public ProcessingReport validateUnchecked(final JsonNode instance)
    {
        return doValidateUnchecked(instance, false);
    }

    /**
     * Check whether an instance is valid against this schema
     *
     * @param instance the instance
     * @return true if the instance is valid
     * @throws ProcessingException an error occurred during processing
     */
    public boolean validInstance(final JsonNode instance)
            throws ProcessingException
    {
        return doValidate(instance, false).isSuccess();
    }

    /**
     * Check whether an instance is valid against this schema (unchecked
     * version)
     *
     * <p>The same warnings apply as described in {@link
     * #validateUnchecked(JsonNode)}.</p>
     *
     * @param instance the instance to validate
     * @return true if the instance is valid
     */
    public boolean validInstanceUnchecked(final JsonNode instance)
    {
        return doValidateUnchecked(instance, false).isSuccess();
    }



    private ProcessingReport doValidate(final JsonNode node, final boolean deepCheck)
            throws ProcessingException
    {
        final FullData data = new FullData(schema, new SimpleJsonTree(node), deepCheck);
        final ProcessingReport report = reportProvider.newReport();
        final ProcessingResult<FullData> result =  ProcessingResult.of(processor, report, data);
        return result.getReport();
    }

    private ProcessingReport doValidateUnchecked(final JsonNode node, final boolean deepCheck)
    {
        final FullData data = new FullData(schema, new SimpleJsonTree(node), deepCheck);
        final ProcessingReport report = reportProvider.newReport();
        final ProcessingResult<FullData> result =  ProcessingResult.uncheckedResult(processor, report, data);
        return result.getReport();
    }

}
