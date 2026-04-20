package SoftwareDesign.demo.api.feedback.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FeedbackDto {
    private Long id;
    private String teacherName;
    private String content;
    private LocalDateTime createdAt;
}