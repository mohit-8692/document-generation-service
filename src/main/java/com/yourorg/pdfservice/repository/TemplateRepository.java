package com.yourorg.pdfservice.repository;

import com.yourorg.pdfservice.exception.TemplateNotFoundException;
import com.yourorg.pdfservice.model.CompiledTemplate;

/**
 * Abstraction over "where templates live". The rest of the service only depends
 * on this interface, so swapping the classpath-backed implementation for a
 * database, S3, or anything else later is a one-class change.
 */
public interface TemplateRepository {

    /**
     * Resolves a specific template + version. Pass version=null to mean "latest".
     */
    CompiledTemplate resolve(String templateId, String version) throws TemplateNotFoundException;

    /**
     * Returns the highest known version string for a templateId.
     */
    String resolveLatestVersion(String templateId) throws TemplateNotFoundException;
}
