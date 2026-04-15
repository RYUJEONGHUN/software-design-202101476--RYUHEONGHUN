package SoftwareDesign.demo.api.feedback.dto;

import SoftwareDesign.demo.domain.feedback.entity.Feedback;
import SoftwareDesign.demo.domain.feedback.entity.FeedbackCategory;
import SoftwareDesign.demo.domain.student.entity.Student;
import SoftwareDesign.demo.domain.teacher.entity.Teacher;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FeedbackResponse {

    private String teacherName;      // 선생님 이름
    private String studentName;      // 학생 이름
    private FeedbackCategory category;
    private String content;
    private LocalDateTime createdAt;

    public static FeedbackResponse from(Feedback feedback) {
        return FeedbackResponse.builder()
                .teacherName(feedback.getTeacher().getUser().getName()) // 연관관계 타고 들어가기
                .studentName(feedback.getStudent().getUser().getName())
                .category(feedback.getCategory())
                .content(feedback.getContent())
                .createdAt(feedback.getCreatedAt())
                .build();
    }

}
