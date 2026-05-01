package SoftwareDesign.demo.api.teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class ScheduledConsultationDto {
    private String studentName;   // 누구와?
    private LocalDate nextPlanDate; // 언제?
}