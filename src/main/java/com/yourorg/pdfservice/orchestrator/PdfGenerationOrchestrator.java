package com.yourorg.pdfservice.orchestrator;

import com.yourorg.pdfservice.dto.request.PdfGenerationRequest;
import com.yourorg.pdfservice.model.CompiledTemplate;
import com.yourorg.pdfservice.service.DataValidationService;
import com.yourorg.pdfservice.service.PdfRenderService;
import com.yourorg.pdfservice.service.TemplateEngineService;
import com.yourorg.pdfservice.service.TemplateResolverService;
import org.springframework.stereotype.Component;

/**
 * Sits between the controller and the individual services. The controller
 * stays a thin HTTP adapter; this class owns the pipeline order and nothing
 * else — it has no HTTP concerns and no rendering/validation logic of its
 * own, only the sequence:
 *
 *   1. resolve templateId(+version) -> CompiledTemplate
 *   2. validate request data against that template's schema
 *   3. merge data into the template -> HTML
 *   4. render HTML -> PDF bytes
 */
@Component
public class PdfGenerationOrchestrator {

    private final TemplateResolverService templateResolverService;
    private final DataValidationService dataValidationService;
    private final TemplateEngineService templateEngineService;
    private final PdfRenderService pdfRenderService;

    public PdfGenerationOrchestrator(TemplateResolverService templateResolverService,
                                      DataValidationService dataValidationService,
                                      TemplateEngineService templateEngineService,
                                      PdfRenderService pdfRenderService) {
        this.templateResolverService = templateResolverService;
        this.dataValidationService = dataValidationService;
        this.templateEngineService = templateEngineService;
        this.pdfRenderService = pdfRenderService;
    }

    public byte[] generate(PdfGenerationRequest request) {
        CompiledTemplate compiledTemplate = templateResolverService.resolve(
                request.getTemplateId(), request.getTemplateVersion());

        dataValidationService.validate(
                compiledTemplate.getTemplateId(),
                compiledTemplate.getVersion(),
                compiledTemplate.getSchema(),
                request.getData());

        String html = templateEngineService.render(compiledTemplate, request.getData());

        return pdfRenderService.renderToPdf(html, request.getOptions());
    }
}
