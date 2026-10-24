package com.yourorg.pdfservice.exception;

public class PdfRenderException extends RuntimeException {

    public PdfRenderException(String message, Throwable cause) {
        super(message, cause);
    }
}
