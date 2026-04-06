package SoftwareDesign.demo.global.config;

import SoftwareDesign.demo.global.auth.JwtAuthenticationFilter;
import SoftwareDesign.demo.global.auth.JwtTokenProvider;
import SoftwareDesign.demo.global.oauth.CustomOAuth2UserService;
import SoftwareDesign.demo.global.oauth.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final JwtTokenProvider tokenProvider; // 필터 생성을 위해 주입

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF 및 세션 비활성화 (REST API 표준)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 2. 권한 설정 (Epic 1 & 2 요구사항 반영)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/users/test-token","/api/v1/users/me").permitAll()
                        .requestMatchers("/", "/login/**", "/oauth2/**", "/favicon.ico").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()  // swagger
                        .requestMatchers("/SoftwareDesign/demo/api/v1/teacher/**").hasRole("TEACHER") // 교사 전용 메뉴
                        .requestMatchers("/api/v1/student/**").hasAnyRole("STUDENT", "TEACHER", "ADMIN") // 학생/교사 공용
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )

                // 3. OAuth2 로그인 설정
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler) // 우리가 만든 핸들러로 JWT 발급!
                )

                // 4. JWT 필터 추가 (UsernamePasswordAuthenticationFilter보다 먼저 실행되어야 함)
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
