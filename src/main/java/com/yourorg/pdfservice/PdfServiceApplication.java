package com.yourorg.pdfservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;

/**
 * Excludes FreeMarkerAutoConfiguration: that autoconfiguration wires a
 * freemarker.template.Configuration bean for rendering MVC *views*. This
 * service is a pure REST API that uses Freemarker to render PDF source
 * templates instead, so we define our own Configuration bean in
 * {@link com.yourorg.pdfservice.config.FreemarkerConfig} and avoid having
 * two competing beans of the same type.
 */
@SpringBootApplication(exclude = FreeMarkerAutoConfiguration.class)
public class PdfServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdfServiceApplication.class, args);
    }
}
