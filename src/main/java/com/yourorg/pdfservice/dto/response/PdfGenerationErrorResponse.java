package com.yourorg.pdfservice.dto.response;

import java.util.List;

public class PdfGenerationErrorResponse {

    private final String errorCode;
    private final String message;
    private final String templateId;
    private final String templateVersion;
    private final List<FieldIssue> details;

    public PdfGenerationErrorResponse(String errorCode, String message, String templateId,
                                       String templateVersion, List<FieldIssue> details) {
        this.errorCode = errorCode;
        this.message = message;
        this.templateId = templateId;
        this.templateVersion = templateVersion;
        this.details = details;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public String getTemplateId() {
        return templateId;
    }

    public String getTemplateVersion() {
        return templateVersion;
    }

    public List<FieldIssue> getDetails() {
        return details;
    }

    public record FieldIssue(String field, String issue) {
    }
}
