package SoftwareDesign.demo.domain.feedback.entity;

import SoftwareDesign.demo.domain.common.BaseTimeEntity;
import SoftwareDesign.demo.domain.student.entity.Student;
import SoftwareDesign.demo.domain.teacher.entity.Teacher;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "feedbacks")
public class Feedback extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private FeedbackCategory category;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private boolean visibleToParent; // 학부모 공개여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher; // 작성자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student; // 대상 학생

    @Builder
    public Feedback(FeedbackCategory category, String content, boolean visibleToParent, Teacher teacher, Student student) {
        this.category = category;
        this.content = content;
        this.visibleToParent = visibleToParent;
        this.teacher = teacher;
        this.student = student;
    }

    public void updateContent(String content, FeedbackCategory feedbackCategory, Boolean visibleToParent){
        if (content != null && !content.isBlank()) {
            this.content = content;
        }
        if (feedbackCategory != null) {
            this.category = feedbackCategory;
        }
        if (visibleToParent != null) {
            this.visibleToParent = visibleToParent;
        }
    }
}
