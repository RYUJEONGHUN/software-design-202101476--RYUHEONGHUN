package SoftwareDesign.demo.api.notification.dto;

import SoftwareDesign.demo.domain.notification.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String message;
    private String type;     // FEEDBACK_CREATED 등
    private String createdAt; // "2026-04-15 15:30" 형식으로 변환해서 주면 프론트가 좋아하네!

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .type(notification.getType().name())
                .createdAt(notification.getCreatedAt().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}