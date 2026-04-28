package SoftwareDesign.demo.domain.consultation.entity;

import SoftwareDesign.demo.domain.common.BaseTimeEntity;
import SoftwareDesign.demo.domain.student.entity.Student;
import SoftwareDesign.demo.domain.teacher.entity.Teacher;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "consultations")
public class Consultation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher; // 상담 교사

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student; // 대상 학생

    private LocalDate consultationDate; // 상담 날짜

    @Column(columnDefinition = "TEXT")
    private String content; // 상담 내용

    @Column(name = "next_plan_date", nullable = true)
    private LocalDate nextPlanDate; // 다음 상담 날짜

    @Builder
    public Consultation(Teacher teacher, Student student, LocalDate consultationDate, String content, LocalDate nextPlanDate) {
        this.teacher = teacher;
        this.student = student;
        this.consultationDate = consultationDate;
        this.content = content;
        this.nextPlanDate = nextPlanDate;
    }
}
