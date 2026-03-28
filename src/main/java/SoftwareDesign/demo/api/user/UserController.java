package SoftwareDesign.demo.api.user;

import SoftwareDesign.demo.api.user.dto.UserResponse;
import SoftwareDesign.demo.domain.user.entity.User;
import SoftwareDesign.demo.domain.user.repository.UserRepository;
import SoftwareDesign.demo.global.auth.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;


    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(@AuthenticationPrincipal String email) {
        return userRepository.findByUsername(email)
                .map(user -> ResponseEntity.ok(new UserResponse(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/test-token")
    public ResponseEntity<?> getTestToken(@RequestParam String email) {
        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        // JwtTokenProvider를 사용해 바로 토큰 생성 (비밀번호 검증 없이!)
        String token = tokenProvider.createAccessToken(user.getUsername(), user.getRole().name());
        return ResponseEntity.ok(token);
    }
}