package SoftwareDesign.demo.domain.common;

import SoftwareDesign.demo.domain.common.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 우리가 만든 커스텀 에러를 잡는 전용 구조대
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus()) // HTTP 상태 코드 (예: 400)
                .body(ApiResponse.error(errorCode)); // JSON 응답 (예: {success: false, ...})
    }

    // 그 외 예상치 못한 모든 런타임 에러를 잡는 일반 구조대
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntimeException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage())); // 메시지 직접 전달용 메서드 필요
    }
}
