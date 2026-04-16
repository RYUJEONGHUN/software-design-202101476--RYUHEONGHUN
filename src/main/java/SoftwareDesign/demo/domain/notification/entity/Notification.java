package SoftwareDesign.demo.domain.notification.entity;

import SoftwareDesign.demo.domain.common.BaseTimeEntity;
import SoftwareDesign.demo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver; // 알림을 받을 사람

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String message;
    private boolean isRead = false; // 읽음 처리 여부

    @Builder
    public Notification(User receiver, NotificationType type, String message) {
        this.receiver = receiver;
        this.type = type;
        this.message = message;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
