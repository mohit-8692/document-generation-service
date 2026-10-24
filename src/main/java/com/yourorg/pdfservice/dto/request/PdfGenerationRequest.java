package com.yourorg.pdfservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * Domain-agnostic request body. The service never knows whether "data" describes
 * an invoice, an order, or an eSIM — it only knows a templateId and a data map.
 */
@Schema(example = """
        {
          "templateId": "invoice-template",
          "data": {
            "invoiceNumber": "INV001",
            "customerName": "John",
            "amount": 1050,
            "isPaid": true,
            "items": [
              { "name": "Laptop", "price": 1000 },
              { "name": "Mouse", "price": 50 }
            ]
          }
        }
        """)
public class PdfGenerationRequest {

    @NotBlank
    @Schema(description = "Identifier of the template to render", example = "invoice-template")
    private String templateId;

    @Schema(description = "Optional. Omit to resolve the latest published version.", example = "1.0.0")
    private String templateVersion;

    @NotNull
    @Schema(description = "Data merged into the template. Shape is validated against the "
            + "resolved template's schema.json.")
    private Map<String, Object> data;

    private PdfGenerationOptions options = PdfGenerationOptions.defaults();

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTemplateVersion() {
        return templateVersion;
    }

    public void setTemplateVersion(String templateVersion) {
        this.templateVersion = templateVersion;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public PdfGenerationOptions getOptions() {
        return options;
    }

    public void setOptions(PdfGenerationOptions options) {
        this.options = options;
    }
}
