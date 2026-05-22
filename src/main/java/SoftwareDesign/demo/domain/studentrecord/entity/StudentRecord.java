package SoftwareDesign.demo.domain.studentrecord.entity;

import SoftwareDesign.demo.domain.common.BaseTimeEntity;
import SoftwareDesign.demo.domain.student.entity.Student;
import SoftwareDesign.demo.global.security.AesGcmStringEncryptor;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "student_records",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_record_student_year_semester",
                        columnNames = {"student_id", "school_year", "semester"}
                )
        }
)
public class StudentRecord extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "school_year", nullable = false)
    private int schoolYear;

    @Column(nullable = false)
    private String semester;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = AesGcmStringEncryptor.class)
    private String specialNote;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = AesGcmStringEncryptor.class)
    private String behaviorNote;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = AesGcmStringEncryptor.class)
    private String careerHope;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = AesGcmStringEncryptor.class)
    private String healthNote;

    @Builder
    public StudentRecord(Student student, int schoolYear, String semester,
                         String specialNote, String behaviorNote,
                         String careerHope, String healthNote) {
        this.student = student;
        this.schoolYear = schoolYear;
        this.semester = semester;
        this.specialNote = specialNote;
        this.behaviorNote = behaviorNote;
        this.careerHope = careerHope;
        this.healthNote = healthNote;
    }

    public void update(String specialNote, String behaviorNote,
                       String careerHope, String healthNote) {
        this.specialNote = specialNote;
        this.behaviorNote = behaviorNote;
        this.careerHope = careerHope;
        this.healthNote = healthNote;
    }
}
