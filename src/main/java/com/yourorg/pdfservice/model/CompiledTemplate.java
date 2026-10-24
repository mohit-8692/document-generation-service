package com.yourorg.pdfservice.model;

import com.fasterxml.jackson.databind.JsonNode;
import freemarker.template.Template;

/**
 * A parsed Freemarker template paired with the JSON Schema that describes the
 * "data" shape it expects. Both are resolved together so the resolver and the
 * validator always agree on which version they're working with.
 */
public class CompiledTemplate {

    private final String templateId;
    private final String version;
    private final Template freemarkerTemplate;
    private final JsonNode schema;

    public CompiledTemplate(String templateId, String version, Template freemarkerTemplate, JsonNode schema) {
        this.templateId = templateId;
        this.version = version;
        this.freemarkerTemplate = freemarkerTemplate;
        this.schema = schema;
    }

    public String getTemplateId() {
        return templateId;
    }

    public String getVersion() {
        return version;
    }

    public Template getFreemarkerTemplate() {
        return freemarkerTemplate;
    }

    public JsonNode getSchema() {
        return schema;
    }
}
