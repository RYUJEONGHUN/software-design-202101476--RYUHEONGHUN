package SoftwareDesign.demo.api.admin.dto;

import SoftwareDesign.demo.domain.user.entity.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRoleUpdateRequest {
    private UserRole role; // 변경할 권한 (STUDENT, TEACHER 등)
}