package com.yourorg.pdfservice.service;

import com.yourorg.pdfservice.exception.PdfRenderException;
import com.yourorg.pdfservice.model.CompiledTemplate;
import freemarker.template.Template;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.Map;

/**
 * Wraps Freemarker's Template.process(...) behind a domain-agnostic method
 * signature: Map<String,Object> in, HTML String out. This is the only class
 * in the service that knows it's Freemarker specifically — swapping engines
 * later means implementing this same shape against a different library.
 */
@Service
public class TemplateEngineService {

    public String render(CompiledTemplate compiledTemplate, Map<String, Object> data) {
        Template template = compiledTemplate.getFreemarkerTemplate();
        StringWriter writer = new StringWriter();
        try {
            template.process(data, writer);
        } catch (Exception e) {
            throw new PdfRenderException(
                    "Failed to render template " + compiledTemplate.getTemplateId()
                            + " v" + compiledTemplate.getVersion(), e);
        }
        return writer.toString();
    }
}
