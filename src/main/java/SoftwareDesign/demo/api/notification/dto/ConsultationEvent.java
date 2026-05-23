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
    private EventAction action;

    public static ConsultationEvent from(Consultation consultation) {
        return from(consultation, EventAction.UPDATED);
    }

    public static ConsultationEvent from(Consultation consultation, EventAction action) {
        return ConsultationEvent.builder()
                .studentId(consultation.getStudent().getId())
                .studentName(consultation.getStudent().getUser().getName())
                .teacherName(consultation.getTeacher().getUser().getName())
                .consultationDate(consultation.getConsultationDate().toString())
                .action(action)
                .build();
    }
}
