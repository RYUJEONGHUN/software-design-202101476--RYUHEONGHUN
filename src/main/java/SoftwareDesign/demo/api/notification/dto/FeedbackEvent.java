package SoftwareDesign.demo.api.notification.dto;

import SoftwareDesign.demo.domain.feedback.entity.Feedback;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackEvent {
    private Long studentId;
    private String teacherName;
    private String content;

    public static FeedbackEvent from(Feedback feedback) {
        return FeedbackEvent.builder()
                .studentId(feedback.getStudent().getId())
                .teacherName(feedback.getTeacher().getUser().getName())
                .content(feedback.getContent())
                .build();
    }
}