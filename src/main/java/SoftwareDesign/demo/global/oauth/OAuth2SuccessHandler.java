package SoftwareDesign.demo.global.oauth;

import SoftwareDesign.demo.global.auth.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.IOException; // 중요: io.jsonwebtoken.io 가 아님!
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider; // 주입받은 빈 사용
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");

        if (email == null) {
            log.error("OAuth2 인증 성공 후 이메일을 찾을 수 없습니다.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email not found from provider");
            return;
        }

        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_GUEST");

        String accessToken = tokenProvider.createAccessToken(email, role);
        String refreshToken = tokenProvider.createRefreshToken(email, role);

        // Redis에 RefreshToken 저장
        redisTemplate.opsForValue().set("RT:" + email, refreshToken, 7, TimeUnit.DAYS);

        // 리프레시 토큰 쿠키 설정 (보안 강화)
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // 로컬(http) 개발 시 false, 운영(https) 시 true
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // 프론트엔드로 AccessToken만 전달 (React 등 프론트 주소)
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/login-success")
                .queryParam("accessToken", accessToken)
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}