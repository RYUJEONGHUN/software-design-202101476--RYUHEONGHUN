package SoftwareDesign.demo.api.notification.dto;

import SoftwareDesign.demo.domain.consultation.entity.Consultation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConsultationEvent {
    private Long studentId;
    private String studentName;
    private String teacherName;
    private String consultationDate;

    public static ConsultationEvent from(Consultation consultation) {
        return ConsultationEvent.builder()
                .studentId(consultation.getStudent().getId())
                .studentName(consultation.getStudent().getUser().getName())
                .teacherName(consultation.getTeacher().getUser().getName())
                .consultationDate(consultation.getConsultationDate().toString())
                .build();
    }
}