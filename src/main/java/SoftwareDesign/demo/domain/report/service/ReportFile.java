package SoftwareDesign.demo.domain.report.service;

import org.springframework.http.MediaType;

public record ReportFile(
        String filename,
        MediaType mediaType,
        byte[] content
) {
}
