package SoftwareDesign.demo.domain.user.service;


import SoftwareDesign.demo.api.admin.dto.UserCreateRequest;
import SoftwareDesign.demo.api.admin.dto.UserRoleUpdateRequest;
import SoftwareDesign.demo.domain.common.ErrorCode;
import SoftwareDesign.demo.domain.common.exception.CustomException;
import SoftwareDesign.demo.domain.user.entity.User;
import SoftwareDesign.demo.domain.user.entity.UserRole;
import SoftwareDesign.demo.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public void registerUser(@Valid UserCreateRequest request){

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_USER);
        }
        User newUser = User.builder()
                .username(request.getUsername())
                .name(request.getName())
                .role(UserRole.GUEST)
                .phoneNumber(request.getPhoneNumber())
                .build();

        userRepository.save(newUser);
    }

    public void updateUserRole(Long user_id, @Valid UserRoleUpdateRequest request){

        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.updateRole(request.getRole());
        userRepository.save(user);

    }

}
