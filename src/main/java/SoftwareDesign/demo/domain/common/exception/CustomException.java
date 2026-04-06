package SoftwareDesign.demo.domain.common.exception;

import SoftwareDesign.demo.domain.common.ErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // 부모인 RuntimeException에도 메시지 전달
        this.errorCode = errorCode;
    }
}