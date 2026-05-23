package SoftwareDesign.demo.domain.analytics.entity;

import SoftwareDesign.demo.domain.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "olap_class_subject_analytics",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_olap_class_subject",
                columnNames = {"semester", "school_grade", "class_num", "subject_id"}
        )
)
public class ClassSubjectAnalytics extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String semester;

    @Column(name = "school_grade", nullable = false)
    private int grade;

    @Column(name = "class_num", nullable = false)
    private int classNum;

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    @Column(nullable = false)
    private String subjectName;

    private long studentCount;
    private long gradeCount;
    private double averageScore;
    private long presentCount;
    private long absentCount;
    private long tardyCount;
    private long excusedCount;
    private long consultationCount;
    private long feedbackCount;

    @Builder
    public ClassSubjectAnalytics(String semester, int grade, int classNum,
                                 Long subjectId, String subjectName,
                                 long studentCount, long gradeCount, double averageScore,
                                 long presentCount, long absentCount, long tardyCount,
                                 long excusedCount, long consultationCount, long feedbackCount) {
        this.semester = semester;
        this.grade = grade;
        this.classNum = classNum;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.studentCount = studentCount;
        this.gradeCount = gradeCount;
        this.averageScore = averageScore;
        this.presentCount = presentCount;
        this.absentCount = absentCount;
        this.tardyCount = tardyCount;
        this.excusedCount = excusedCount;
        this.consultationCount = consultationCount;
        this.feedbackCount = feedbackCount;
    }
}
