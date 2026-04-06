package SoftwareDesign.demo.domain.grade.entity;

import SoftwareDesign.demo.domain.common.BaseTimeEntity;
import SoftwareDesign.demo.domain.student.entity.Student;
import SoftwareDesign.demo.domain.subject.entity.Subject;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "grades")
public class Grade extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    private String semester; // 학기 (예: "2026-1")

    private String letterGrade;

    @Builder
    public Grade(Student student, Subject subject, int score, String semester) {
        this.student = student;
        this.subject = subject;
        this.score = score;
        this.semester = semester;
        this.letterGrade = calculateLetterGrade(score); // 생성 시 자동 계산!
    }

    private String calculateLetterGrade(int score) {
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }

    // 성적 수정 시 호출할 메서드
    public void updateScore(int newScore) {
        this.score = newScore;
        this.letterGrade = calculateLetterGrade(newScore);
    }


}
