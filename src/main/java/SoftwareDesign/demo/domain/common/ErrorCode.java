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
    STUDENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 학생을 찾을 수 없네."),
    TEACHER_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 선생을 찾을 수 없네."),
    PARENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 학부모를 찾을 수 없네."),
    SUBJECT_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 과목을 찾을 수 없네."),
    ALREADY_GRADE_EXIST(HttpStatus.BAD_REQUEST, "이미 이 학기에 해당 과목 성적이 등록되어 있구먼!"),
    ALREADY_REGISTERED_USER(HttpStatus.BAD_REQUEST, "해당 유저가 이미 있어"),
    ALREADY_REGISTERED_STUDENT(HttpStatus.BAD_REQUEST, "이미 해당 학생이 있어"),
    ALREADY_REGISTERED_TEACHER(HttpStatus.BAD_REQUEST, "이미 해당 선생이 있어"),
    ALREADY_REGISTERED_PARENT(HttpStatus.BAD_REQUEST, "이미 해당 학부모가 있어"),
    ALREADY_ATTENDANCE_CHECKED(HttpStatus.BAD_REQUEST, "이미 출석체크 처리되어있어"),
    INVALID_SCORE(HttpStatus.BAD_REQUEST, "성적은 0점에서 100점 사이여야 하네."),

    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요한 서비스."),

    // 403 Forbidden
    FORBIDDEN(HttpStatus.FORBIDDEN, "자네는 권한이 없구먼!"),
    NOT_TEACHER(HttpStatus.FORBIDDEN, "자네는 선생님이 아니라서 성적을 넣을 수 없네!"),
    NOT_STUDENT(HttpStatus.FORBIDDEN, "자네는 학생이 아니구먼!"),
    NOT_YOUR_CHILD(HttpStatus.FORBIDDEN, "당신의 자녀가 아닙니다"),
    NOT_YOUR_FEEDBACK(HttpStatus.FORBIDDEN, "당신이 작성한 피드백이 아닙니다"),
    NOT_YOUR_NOTIFICATION(HttpStatus.FORBIDDEN,"당신의 알람이 아닙니다"),
    // 404 Not Found
    DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "요청하신 데이터를 찾을 수 없구먼."),
    ATTENDANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "출석정보를 찾을 수 없다"),
    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버가 좀 아픈가 보네...");

    private final HttpStatus httpStatus;
    private final String message;
}
