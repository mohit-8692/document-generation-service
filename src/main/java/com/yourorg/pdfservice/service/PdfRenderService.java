package com.yourorg.pdfservice.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.yourorg.pdfservice.dto.request.PdfGenerationOptions;
import com.yourorg.pdfservice.exception.PdfRenderException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

/**
 * Final stage of the pipeline: takes fully-rendered HTML (already merged with
 * data by TemplateEngineService) and produces PDF bytes. This class knows
 * nothing about templates, data, or domains — it only knows HTML in, PDF out.
 */
@Service
public class PdfRenderService {

    public byte[] renderToPdf(String html, PdfGenerationOptions options) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);

            // Page size / orientation are CSS-level concerns for OpenHTMLToPDF (@page rules).
            // Templates that care about a specific page size should declare it themselves
            // via @page { size: A4 landscape; } — options.pageSize/orientation are passed
            // through here as a hook for a future default-stylesheet injection step.
            builder.run();

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new PdfRenderException("Failed to render HTML to PDF", e);
        }
    }
}
