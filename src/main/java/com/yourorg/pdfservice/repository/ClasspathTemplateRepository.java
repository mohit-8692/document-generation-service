package com.yourorg.pdfservice.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourorg.pdfservice.exception.TemplateCompileException;
import com.yourorg.pdfservice.exception.TemplateNotFoundException;
import com.yourorg.pdfservice.model.CompiledTemplate;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Discovers templates laid out on the classpath as:
 *
 *   templates/{templateId}/{version}/template.ftl
 *   templates/{templateId}/{version}/schema.json
 *
 * Every template folder is bundled into the service's own JAR at build time —
 * there is no external template store. Adding a new template or a new version
 * means adding files under src/main/resources/templates and shipping a new
 * build. This trades away "publish a template without redeploying the
 * service" in exchange for zero external moving parts, which is the
 * classpath-only option that was chosen for this iteration.
 */
@Repository
public class ClasspathTemplateRepository implements TemplateRepository {

    private static final Logger log = LoggerFactory.getLogger(ClasspathTemplateRepository.class);
    private static final String TEMPLATE_FILE = "template.ftl";
    private static final String SCHEMA_FILE = "schema.json";
    private static final String DISCOVERY_PATTERN = "classpath*:templates/*/*/" + TEMPLATE_FILE;

    private final Configuration freemarkerConfiguration;
    private final ObjectMapper objectMapper;

    /** templateId -> (version -> present), sorted ascending by a simple semver comparator. */
    private final Map<String, TreeMap<String, Boolean>> versionIndex = new ConcurrentHashMap<>();

    public ClasspathTemplateRepository(Configuration freemarkerConfiguration, ObjectMapper objectMapper) {
        this.freemarkerConfiguration = freemarkerConfiguration;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    void buildVersionIndex() {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources(DISCOVERY_PATTERN);
            for (Resource resource : resources) {
                String uri = resource.getURI().toString();
                int marker = uri.indexOf("templates/");
                if (marker < 0) {
                    continue;
                }
                String relative = uri.substring(marker + "templates/".length());
                String[] parts = relative.split("/");
                if (parts.length < 3) {
                    continue;
                }
                String templateId = parts[0];
                String version = parts[1];
                versionIndex
                        .computeIfAbsent(templateId, id -> new TreeMap<>(SemVerComparator.INSTANCE))
                        .put(version, Boolean.TRUE);
            }
            log.info("Discovered {} template id(s) on classpath: {}", versionIndex.size(), versionIndex.keySet());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to scan classpath for templates", e);
        }
    }

    @Override
    public CompiledTemplate resolve(String templateId, String requestedVersion) {
        String version = requestedVersion != null ? requestedVersion : resolveLatestVersion(templateId);
        assertVersionExists(templateId, version);

        String basePath = templateId + "/" + version + "/";
        Template template = compile(templateId, version, basePath + TEMPLATE_FILE);
        JsonNode schema = readSchema(templateId, version, basePath + SCHEMA_FILE);

        return new CompiledTemplate(templateId, version, template, schema);
    }

    @Override
    public String resolveLatestVersion(String templateId) {
        TreeMap<String, Boolean> versions = versionIndex.get(templateId);
        if (versions == null || versions.isEmpty()) {
            throw new TemplateNotFoundException(templateId, null);
        }
        return versions.lastKey();
    }

    private void assertVersionExists(String templateId, String version) {
        TreeMap<String, Boolean> versions = versionIndex.get(templateId);
        if (versions == null || !versions.containsKey(version)) {
            throw new TemplateNotFoundException(templateId, version);
        }
    }

    private Template compile(String templateId, String version, String pathRelativeToLoaderRoot) {
        try {
            // freemarkerConfiguration's ClassTemplateLoader root is "/templates", so this
            // resolves to classpath:/templates/{templateId}/{version}/template.ftl. Freemarker
            // caches compiled templates internally keyed by this path, so repeated calls for
            // the same version are cheap without any caching logic of our own here.
            return freemarkerConfiguration.getTemplate(pathRelativeToLoaderRoot);
        } catch (IOException e) {
            throw new TemplateCompileException(templateId, version, e);
        }
    }

    private JsonNode readSchema(String templateId, String version, String classpathRelativePath) {
        String resourcePath = "templates/" + classpathRelativePath;
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new TemplateNotFoundException(templateId, version);
            }
            return objectMapper.readTree(in);
        } catch (IOException e) {
            throw new TemplateCompileException(templateId, version, e);
        }
    }

    /**
     * Compares version strings like "1.4.0" numerically component-by-component
     * instead of lexicographically (so "1.10.0" sorts after "1.9.0", not before).
     * Falls back to plain string comparison for anything that isn't dot-numeric.
     */
    private static final class SemVerComparator implements Comparator<String> {
        static final SemVerComparator INSTANCE = new SemVerComparator();

        @Override
        public int compare(String a, String b) {
            String[] partsA = a.split("\\.");
            String[] partsB = b.split("\\.");
            int length = Math.max(partsA.length, partsB.length);
            for (int i = 0; i < length; i++) {
                int va = part(partsA, i);
                int vb = part(partsB, i);
                if (va != vb) {
                    return Integer.compare(va, vb);
                }
            }
            return 0;
        }

        private int part(String[] parts, int index) {
            if (index >= parts.length) {
                return 0;
            }
            try {
                return Integer.parseInt(parts[index]);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }
}
