package SoftwareDesign.demo.domain.common;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 이 클래스를 상속받는 엔티티들이 아래 필드들을 컬럼으로 인식
@EntityListeners(AuditingEntityListener.class) //  자동으로 시간을 넣어주는 리스너라네.
public abstract class BaseTimeEntity {

    @CreatedDate // 생성 시 자동 기록
    private LocalDateTime createdAt;

    @LastModifiedDate // 수정 시 자동 기록
    private LocalDateTime updatedAt;
}