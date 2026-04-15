package SoftwareDesign.demo.domain.notification.repository;

import SoftwareDesign.demo.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    // 특정 유저의 알림 중 읽지 않은 것만 최신순으로 조회
    List<Notification> findAllByReceiverIdAndIsReadFalseOrderByCreatedAtDesc(Long receiverId);
}
