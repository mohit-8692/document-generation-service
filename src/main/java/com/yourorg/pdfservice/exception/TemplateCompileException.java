package com.yourorg.pdfservice.exception;

public class TemplateCompileException extends RuntimeException {

    private final String templateId;
    private final String templateVersion;

    public TemplateCompileException(String templateId, String templateVersion, Throwable cause) {
        super("Failed to compile template " + templateId + " v" + templateVersion, cause);
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
