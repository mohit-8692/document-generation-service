package com.yourorg.pdfservice.exception;

import com.yourorg.pdfservice.dto.response.PdfGenerationErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class PdfServiceExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(PdfServiceExceptionHandler.class);

    @ExceptionHandler(TemplateNotFoundException.class)
    public ResponseEntity<PdfGenerationErrorResponse> handleNotFound(TemplateNotFoundException ex) {
        PdfGenerationErrorResponse body = new PdfGenerationErrorResponse(
                "TEMPLATE_NOT_FOUND", ex.getMessage(), ex.getTemplateId(), ex.getTemplateVersion(), List.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(TemplateDataMismatchException.class)
    public ResponseEntity<PdfGenerationErrorResponse> handleDataMismatch(TemplateDataMismatchException ex) {
        PdfGenerationErrorResponse body = new PdfGenerationErrorResponse(
                "TEMPLATE_DATA_MISMATCH", ex.getMessage(), ex.getTemplateId(), ex.getTemplateVersion(), ex.getIssues());
        return ResponseEntity.unprocessableEntity().body(body);
    }

    @ExceptionHandler(TemplateCompileException.class)
    public ResponseEntity<PdfGenerationErrorResponse> handleCompile(TemplateCompileException ex) {
        // Should be rare in production if templates are validated before being added to the classpath —
        // treat as a server-side problem worth alerting on rather than a client error.
        log.error("Template compile failure", ex);
        PdfGenerationErrorResponse body = new PdfGenerationErrorResponse(
                "TEMPLATE_COMPILE_ERROR", ex.getMessage(), ex.getTemplateId(), ex.getTemplateVersion(), List.of());
        return ResponseEntity.internalServerError().body(body);
    }

    @ExceptionHandler(PdfRenderException.class)
    public ResponseEntity<PdfGenerationErrorResponse> handleRenderFailure(PdfRenderException ex) {
        log.error("PDF render failure", ex);
        PdfGenerationErrorResponse body = new PdfGenerationErrorResponse(
                "RENDER_FAILURE", ex.getMessage(), null, null, List.of());
        return ResponseEntity.internalServerError().body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<PdfGenerationErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<PdfGenerationErrorResponse.FieldIssue> issues = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new PdfGenerationErrorResponse.FieldIssue(fe.getField(), fe.getDefaultMessage()))
                .toList();
        PdfGenerationErrorResponse body = new PdfGenerationErrorResponse(
                "REQUEST_VALIDATION_ERROR", "Request body failed validation", null, null, issues);
        return ResponseEntity.badRequest().body(body);
    }
}
