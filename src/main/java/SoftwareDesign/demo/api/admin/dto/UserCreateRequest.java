package SoftwareDesign.demo.api.admin.dto;

import SoftwareDesign.demo.domain.user.entity.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserCreateRequest {
    private String username; // 구글 이메일 혹은 고유 ID
    private String name;
    private String phoneNumber;
}
