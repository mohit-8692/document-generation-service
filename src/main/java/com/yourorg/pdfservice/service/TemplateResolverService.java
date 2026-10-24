package com.yourorg.pdfservice.service;

import com.yourorg.pdfservice.model.CompiledTemplate;
import com.yourorg.pdfservice.repository.TemplateRepository;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Sits between the orchestrator and the repository. Freemarker already caches
 * compiled Template objects internally by path, but we still cache the
 * CompiledTemplate (template + schema pair) here to avoid re-reading and
 * re-parsing schema.json on every request, and to give the service one clear
 * place to add eviction later if templates ever become mutable at runtime.
 */
@Service
public class TemplateResolverService {

    private final TemplateRepository templateRepository;
    private final ConcurrentHashMap<String, CompiledTemplate> cache = new ConcurrentHashMap<>();

    public TemplateResolverService(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    public CompiledTemplate resolve(String templateId, String requestedVersion) {
        String version = requestedVersion != null
                ? requestedVersion
                : templateRepository.resolveLatestVersion(templateId);

        String cacheKey = templateId + ":" + version;
        return cache.computeIfAbsent(cacheKey, key -> templateRepository.resolve(templateId, version));
    }
}
