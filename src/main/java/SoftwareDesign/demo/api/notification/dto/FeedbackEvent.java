package SoftwareDesign.demo.api.notification.dto;

import SoftwareDesign.demo.domain.feedback.entity.Feedback;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackEvent {
    private Long studentId;
    private String teacherName;
    private LocalDate createdDate;
    private String content;
    private EventAction action;

    public static FeedbackEvent from(Feedback feedback) {
        return from(feedback, EventAction.UPDATED);
    }

    public static FeedbackEvent from(Feedback feedback, EventAction action) {
        return FeedbackEvent.builder()
                .studentId(feedback.getStudent().getId())
                .teacherName(feedback.getTeacher().getUser().getName())
                .createdDate(LocalDate.now())
                .content(feedback.getContent())
                .action(action)
                .build();
    }
}
