package SoftwareDesign.demo.api.user.dto;

import SoftwareDesign.demo.domain.user.entity.User;
import SoftwareDesign.demo.domain.user.entity.UserRole;
import lombok.Getter;

@Getter
public class UserResponse {
    private final String email;
    private final String name;
    private final UserRole role;

    public UserResponse(User user) {
        this.email = user.getUsername();
        this.name = user.getName();
        this.role = user.getRole();
    }
}