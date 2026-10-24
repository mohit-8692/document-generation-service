package com.yourorg.pdfservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SchemaValidatorsConfig;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.yourorg.pdfservice.dto.response.PdfGenerationErrorResponse.FieldIssue;
import com.yourorg.pdfservice.exception.TemplateDataMismatchException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Validates a request's "data" map against a template's schema.json *before*
 * that data ever reaches the Freemarker engine. This is what turns a missing
 * field into a clean 422 with a field name, instead of a half-rendered PDF or
 * a raw NullPointerException surfacing from inside template rendering.
 */
@Service
public class DataValidationService {

    private final JsonSchemaFactory schemaFactory =
            JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
    private final ObjectMapper objectMapper;

    public DataValidationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void validate(String templateId, String templateVersion, JsonNode schema, Map<String, Object> data) {
        SchemaValidatorsConfig config = new SchemaValidatorsConfig();
        JsonSchema jsonSchema = schemaFactory.getSchema(schema, config);

        JsonNode dataNode = objectMapper.valueToTree(data);
        Set<ValidationMessage> violations = jsonSchema.validate(dataNode);

        if (!violations.isEmpty()) {
            List<FieldIssue> issues = violations.stream()
                    .map(v -> new FieldIssue(v.getInstanceLocation().toString(), v.getMessage()))
                    .toList();
            throw new TemplateDataMismatchException(templateId, templateVersion, issues);
        }
    }
}
