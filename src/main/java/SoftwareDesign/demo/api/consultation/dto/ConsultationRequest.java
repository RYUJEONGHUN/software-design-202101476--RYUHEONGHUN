package SoftwareDesign.demo.api.consultation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ConsultationRequest {
    private Long studentId;       // 상담 대상 학생 ID
    private LocalDate consultationDate; // 상담 날짜
    private String content;       // 상담 내용

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate nextPlanDate;     // 다음 상담 날짜
}