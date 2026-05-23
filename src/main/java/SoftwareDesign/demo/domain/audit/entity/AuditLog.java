package SoftwareDesign.demo.domain.audit.entity;

import SoftwareDesign.demo.domain.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "audit_logs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String eventType;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false, length = 100)
    private String routingKey;

    @Column(nullable = false, length = 500)
    private String summary;

    @Lob
    @Column(nullable = false, columnDefinition = "text")
    private String payload;

    @Builder
    public AuditLog(String eventType, Long studentId, String routingKey, String summary, String payload) {
        this.eventType = eventType;
        this.studentId = studentId;
        this.routingKey = routingKey;
        this.summary = summary;
        this.payload = payload;
    }
}
