package SoftwareDesign.demo.api.consultation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ConsultationUpdateRequest {
    private LocalDate consultationDate;
    private String content;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate nextPlanDate;
}