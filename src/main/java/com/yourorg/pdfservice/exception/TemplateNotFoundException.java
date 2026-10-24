package com.yourorg.pdfservice.exception;

public class TemplateNotFoundException extends RuntimeException {

    private final String templateId;
    private final String templateVersion;

    public TemplateNotFoundException(String templateId, String templateVersion) {
        super("Template not found: " + templateId + (templateVersion != null ? " v" + templateVersion : ""));
        this.templateId = templateId;
        this.templateVersion = templateVersion;
    }

    public String getTemplateId() {
        return templateId;
    }

    public String getTemplateVersion() {
        return templateVersion;
    }
}
