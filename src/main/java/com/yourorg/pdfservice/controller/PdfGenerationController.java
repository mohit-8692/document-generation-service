package com.yourorg.pdfservice.controller;

import com.yourorg.pdfservice.dto.request.PdfGenerationRequest;
import com.yourorg.pdfservice.dto.response.PdfGenerationErrorResponse;
import com.yourorg.pdfservice.orchestrator.PdfGenerationOrchestrator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP boundary only. Deliberately thin: no template resolution, validation,
 * or rendering logic lives here — that's the orchestrator's job. This class
 * exists so the pipeline is reachable over REST and so HTTP-specific concerns
 * (headers, status codes, content type) stay separate from the pipeline logic
 * itself, which makes the orchestrator trivially reusable from a queue
 * consumer or a batch job later without touching this class.
 */
@RestController
@RequestMapping("/pdf")
@Tag(name = "PDF generation", description = "Renders a template + data payload into a PDF")
public class PdfGenerationController {

    private final PdfGenerationOrchestrator orchestrator;

    public PdfGenerationController(PdfGenerationOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Operation(
            summary = "Generate a PDF",
            description = "Resolves the given templateId (+ optional version), validates \"data\" "
                    + "against that template's JSON Schema, renders it, and streams back the PDF."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PDF generated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_PDF_VALUE)),
            @ApiResponse(responseCode = "404", description = "Template or version not found",
                    content = @Content(schema = @Schema(implementation = PdfGenerationErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Request data failed schema validation",
                    content = @Content(schema = @Schema(implementation = PdfGenerationErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Template compile or render failure",
                    content = @Content(schema = @Schema(implementation = PdfGenerationErrorResponse.class)))
    })
    @PostMapping(value = "/generate", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generate(@Valid @RequestBody PdfGenerationRequest request) {
        byte[] pdf = orchestrator.generate(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + request.getTemplateId() + ".pdf\"")
                .header("X-Template-Id", request.getTemplateId())
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}

