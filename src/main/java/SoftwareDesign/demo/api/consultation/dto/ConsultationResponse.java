package SoftwareDesign.demo.api.consultation.dto;

import SoftwareDesign.demo.domain.consultation.entity.Consultation;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ConsultationResponse {
    private Long id;
    private String studentName;    // 상담 대상 학생 이름
    private String studentNumber;  // 학번
    private String teacherName;    // 상담 교사 이름
    private LocalDate consultationDate; // 상담 날짜
    private String content;        // 상담 내용
    private String nextPlan;       // 다음 상담 계획
    private LocalDateTime createdAt; // 기록 생성 시간

    /**
     * 엔티티를 DTO로 변환하는 생성자
     */
    public ConsultationResponse(Consultation consultation) {
        this.id = consultation.getId();
        this.consultationDate = consultation.getConsultationDate();
        this.content = consultation.getContent();
        this.nextPlan = consultation.getNextPlan();
        this.createdAt = consultation.getCreatedAt();

        if (consultation.getStudent() != null && consultation.getStudent().getUser() != null) {
            this.studentName = consultation.getStudent().getUser().getName();
            this.studentNumber = consultation.getStudent().getStudentNumber();
        }

        if (consultation.getTeacher() != null && consultation.getTeacher().getUser() != null) {
            this.teacherName = consultation.getTeacher().getUser().getName();
        }
    }
}