package SoftwareDesign.demo.api.notification;

import SoftwareDesign.demo.api.notification.dto.NotificationResponse;
import SoftwareDesign.demo.domain.common.ApiResponse;
import SoftwareDesign.demo.domain.common.ErrorCode;
import SoftwareDesign.demo.domain.common.SuccessCode;
import SoftwareDesign.demo.domain.common.exception.CustomException;
import SoftwareDesign.demo.domain.notification.entity.Notification;
import SoftwareDesign.demo.domain.notification.repository.NotificationRepository;
import SoftwareDesign.demo.domain.notification.service.NotificationService;
import SoftwareDesign.demo.domain.user.entity.User;
import SoftwareDesign.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(Authentication authentication) {
        // 유저 정보를 가져와서 구독
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        Long userId = user.getId();
        return notificationService.subscribe(userId);
    }

    // 1. 읽지 않은 알림 목록 조회
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnreadNotifications(
            Authentication authentication) {

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        Long userId = user.getId();

        List<NotificationResponse> responses = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.GET_SUCCESS, responses));
    }

    // 2. 알림 읽음 처리 (단건)
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<String>> markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication) {

        notificationService.markAsRead(authentication.getName(), notificationId);

        return ResponseEntity.ok(ApiResponse.success(SuccessCode.UPDATE_SUCCESS, "알림 읽음 처리 완료!"));
    }
}