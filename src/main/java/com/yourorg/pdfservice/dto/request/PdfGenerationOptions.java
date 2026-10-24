package com.yourorg.pdfservice.dto.request;

public class PdfGenerationOptions {

    private String locale = "en-US";
    private String pageSize = "A4";
    private String orientation = "portrait";

    public static PdfGenerationOptions defaults() {
        return new PdfGenerationOptions();
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }
}
