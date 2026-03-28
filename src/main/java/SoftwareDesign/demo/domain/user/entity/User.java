package SoftwareDesign.demo.domain.user.entity;

import SoftwareDesign.demo.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username; // 구글 이메일 혹은 고유 ID

    @Column // 일반 로그인 안 쓸 경우 null 허용
    private String passwordHash;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Builder
    public User(String username, String name, UserRole role, String phoneNumber) {
        this.username = username;
        this.name = name;
        this.role = role;
        this.phoneNumber = phoneNumber;
    }

    // OAuth2 정보 업데이트 로직
    public User update(String name) {
        this.name = name;
        return this;
    }
    // User.java 내부
    public void updateRole(UserRole role) {
        this.role = role;
    }
}
