package com.yourorg.pdfservice.config;

import freemarker.cache.ClassTemplateLoader;
import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.context.annotation.Bean;

/**
 * Defines the single freemarker.template.Configuration used to compile and
 * render PDF source templates. Kept intentionally restrictive:
 *  - ClassTemplateLoader roots at "/templates" on the classpath, so a
 *    template can never reference a path outside that tree.
 *  - DefaultObjectWrapperBuilder (rather than an unrestricted wrapper) keeps
 *    templates from reaching into arbitrary Java class internals via
 *    reflection — they can only read properties/methods exposed on the data
 *    map you pass in.
 *  - HTML_OUTPUT_FORMAT + auto-escaping means values from request "data"
 *    (e.g. a customer-supplied name field) are HTML-escaped by default,
 *    which matters because that HTML is fed straight into a PDF renderer.
 */
@org.springframework.context.annotation.Configuration
public class FreemarkerConfig {

    @Bean
    public Configuration freemarkerConfiguration() {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);

        configuration.setTemplateLoader(new ClassTemplateLoader(getClass(), "/templates"));
        configuration.setDefaultEncoding("UTF-8");
        configuration.setOutputFormat(HTMLOutputFormat.INSTANCE);
        configuration.setAutoEscapingPolicy(Configuration.ENABLE_IF_DEFAULT_AUTO_ESCAPING_POLICY);

        // Fail loudly on a missing variable rather than silently rendering blank —
        // this is what turns a typo'd placeholder into a caught bug instead of a
        // shipped document with a hole in it.
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        // Restricted object wrapper: templates can access bean properties and map
        // entries, but not arbitrary Java reflection (no .class, no static access).
        configuration.setObjectWrapper(new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_32).build());

        // Templates should never load other templates by absolute/parent-relative
        // path tricks — keep everything resolvable strictly within the loader root.
        configuration.setAPIBuiltinEnabled(false);

        return configuration;
    }
}
