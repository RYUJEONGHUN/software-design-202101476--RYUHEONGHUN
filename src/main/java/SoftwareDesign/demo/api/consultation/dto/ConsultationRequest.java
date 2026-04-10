package SoftwareDesign.demo.api.consultation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ConsultationRequest {
    private Long studentId;       // 상담 대상 학생 ID
    private LocalDate consultationDate; // 상담 날짜
    private String content;       // 상담 내용
    private String nextPlan;      // 다음 상담 계획 (SOF-31)
}