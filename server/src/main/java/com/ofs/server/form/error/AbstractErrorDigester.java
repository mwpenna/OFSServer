package com.ofs.server.form.error;

import com.fasterxml.jackson.databind.JsonNode;
import com.ofs.server.model.OFSErrors;
import com.ofs.server.model.OFSError;
import com.ofs.server.utils.CaseFormat;
import org.springframework.util.StringUtils;

import static com.ofs.server.utils.CaseFormat.LOWER_CAMEL;
import static com.ofs.server.utils.CaseFormat.LOWER_UNDERSCORE;
import static com.ofs.server.utils.CaseFormat.forString;
import static com.ofs.server.utils.Objects.ifNull;
import static java.lang.String.format;


public abstract class AbstractErrorDigester implements ErrorDigester {


    private String formatField(String ptr)
    {
        StringBuilder builder = new StringBuilder();
        for(String part : ptr.split("/")) {
            if(builder.length() > 0) builder.append(".");
            if(parse(part, -1) != -1) {
                builder.append("items");
            } else {
                builder.append(part);
            }
        }
        return builder.toString();
    }

    private String formatCode(String ptr)
    {
        StringBuilder builder = new StringBuilder();
        for(String part : ptr.split("/")) {
            if(builder.length() > 0) builder.append(".");
            if(parse(part, -1) != -1) {
                builder.append("items");
            } else {
                CaseFormat format = ifNull(forString(part), LOWER_CAMEL);
                builder.append(format.to(LOWER_UNDERSCORE, part));
            }
        }
        return builder.toString();
    }

    protected OFSError reject(OFSErrors errors, String code, String entity, String field, String message)
    {
        if(StringUtils.isEmpty(field)) {
            String formatted = format("%s.%s", entity, code);
            return errors.rejectValue(formatted, message);
        } else {
            String formatted = format("%s.%s.%s", entity, formatCode(field), code);
            return errors.rejectValue(formatted, formatField(field), message);
        }
    }

    protected String asText(JsonNode node)
    {
        if(node != null) {
            if (node.isBigDecimal()) {
                return node.decimalValue().toPlainString();
            }
            return node.asText();
        }
        return "";
    }

    private static int parse(CharSequence str, int def) {
        try {
            return Integer.parseInt(toString(str));
        } catch (Exception var3) {
            return def;
        }
    }

    public static String toString(CharSequence seq) {
        return seq != null?seq.toString():null;
    }
}
