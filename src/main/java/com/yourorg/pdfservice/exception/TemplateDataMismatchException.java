package com.yourorg.pdfservice.exception;

import com.yourorg.pdfservice.dto.response.PdfGenerationErrorResponse.FieldIssue;

import java.util.List;

public class TemplateDataMismatchException extends RuntimeException {

    private final String templateId;
    private final String templateVersion;
    private final List<FieldIssue> issues;

    public TemplateDataMismatchException(String templateId, String templateVersion, List<FieldIssue> issues) {
        super("Data does not match schema for template " + templateId + " v" + templateVersion
                + ": " + issues.size() + " issue(s)");
        this.templateId = templateId;
        this.templateVersion = templateVersion;
        this.issues = issues;
    }

    public String getTemplateId() {
        return templateId;
    }

    public String getTemplateVersion() {
        return templateVersion;
    }

    public List<FieldIssue> getIssues() {
        return issues;
    }
}
