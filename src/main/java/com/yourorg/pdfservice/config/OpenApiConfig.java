package com.yourorg.pdfservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI pdfServiceOpenApi() {
        return new OpenAPI().info(new Info()
                .title("PDF generation service")
                .description("Generic, domain-agnostic PDF generation microservice. "
                        + "Consumers submit a templateId (+ optional version) and a data payload; "
                        + "the service resolves the matching Freemarker template, validates data "
                        + "against its JSON Schema, and returns a rendered PDF.")
                .version("0.1.0"));
    }
}
