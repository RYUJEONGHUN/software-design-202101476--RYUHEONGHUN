package SoftwareDesign.demo.domain.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;

    // 성공 시 호출
    public static <T> ApiResponse<T> success(SuccessCode code, T data) {
        return new ApiResponse<>(true, data, code.getMessage());
    }

    // 실패 시 호출
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(false, null, errorCode.getMessage());
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message);
    }
}