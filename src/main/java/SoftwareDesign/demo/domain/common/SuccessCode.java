package SoftwareDesign.demo.domain.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode {
    // 200 OK
    GET_SUCCESS(HttpStatus.OK, "조회에 성공했구먼!"),
    UPDATE_SUCCESS(HttpStatus.OK, "수정이 아주 잘 됐네."),
    DELETE_SUCCESS(HttpStatus.OK, "삭제가 완료되었네."),

    // 201 Created
    CREATE_SUCCESS(HttpStatus.CREATED, "새로운 데이터가 생성되었네!");

    private final HttpStatus httpStatus;
    private final String message;
}
