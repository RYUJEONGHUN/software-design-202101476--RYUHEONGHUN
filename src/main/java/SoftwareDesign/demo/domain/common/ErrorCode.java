package SoftwareDesign.demo.domain.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 400 Bad Request
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않구먼!"),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 유저를 찾을 수 없네."),

    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요한 서비스라네."),

    // 403 Forbidden
    FORBIDDEN(HttpStatus.FORBIDDEN, "자네는 권한이 없구먼!"),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 삼촌이 좀 아픈가 보네...");

    private final HttpStatus httpStatus;
    private final String message;
}
