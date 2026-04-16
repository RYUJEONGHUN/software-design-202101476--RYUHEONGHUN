package SoftwareDesign.demo.domain.notification.service;

import SoftwareDesign.demo.api.notification.dto.NotificationResponse;
import SoftwareDesign.demo.domain.common.ErrorCode;
import SoftwareDesign.demo.domain.common.exception.CustomException;
import SoftwareDesign.demo.domain.notification.entity.Notification;
import SoftwareDesign.demo.domain.notification.entity.NotificationType;
import SoftwareDesign.demo.domain.notification.repository.NotificationRepository;
import SoftwareDesign.demo.domain.user.entity.User;
import SoftwareDesign.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    // 클라이언트와의 SSE 연결을 저장하는 저장소
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // 클라이언트가 SSE 연결을 위해 호출하는 메서드

    public SseEmitter subscribe(Long userId) {
        // 유효시간 설정 (보통 1시간)
        SseEmitter emitter = new SseEmitter(60L * 1000 * 60);

        // 연결 저장
        emitters.put(userId, emitter);

        // 연결이 끝나거나 에러가 나면 리스트에서 삭제
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError((e) -> emitters.remove(userId));

        // 4. 503 에러 방지를 위한 더미 데이터 전송
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected!"));
        } catch (IOException e) {
            log.error("SSE 연결 중 에러 발생: {}", e.getMessage());
        }

        return emitter;
    }

    @Transactional
    public void send(Long receiverId, NotificationType type, String message){
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // DB에 알림 저장
        Notification notification = Notification.builder()
                .receiver(receiver)
                .type(type)
                .message(message)
                .build();
        notificationRepository.save(notification);

        // 실시간 전송 (SSE 통로가 열려있다면)
        if (emitters.containsKey(receiver.getId())) {
            SseEmitter emitter = emitters.get(receiver.getId());
            try {
                emitter.send(SseEmitter.event()
                        .id(notification.getId().toString())
                        .name("notification") // 이벤트 이름
                        .data(message));      // 보낼 내용
            } catch (IOException e) {
                // 연결이 끊겼다면 삭제
                emitters.remove(receiver.getId());
            }
        }
    }
    // 알림 목록 조회
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        return notificationRepository.findAllByReceiverIdAndIsReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
    }

    // 알림 읽음 처리
    @Transactional
    public void markAsRead(String email, Long notificationId) {
        // 알림 조회
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.DATA_NOT_FOUND));

        // 유저 조회
        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 본인 확인
        if (!notification.getReceiver().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.NOT_YOUR_NOTIFICATION);
        }

        // 4. 상태 변경
        notification.markAsRead();
    }
}