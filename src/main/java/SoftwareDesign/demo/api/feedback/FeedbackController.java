package SoftwareDesign.demo.api.feedback;

import SoftwareDesign.demo.api.feedback.dto.FeedbackCreateRequest;
import SoftwareDesign.demo.api.feedback.dto.FeedbackResponse;
import SoftwareDesign.demo.api.feedback.dto.FeedbackUpdateRequest;
import SoftwareDesign.demo.domain.common.ApiResponse;
import SoftwareDesign.demo.domain.common.SuccessCode;
import SoftwareDesign.demo.domain.feedback.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/feedbacks")
@RequiredArgsConstructor
public class FeedbackController implements FeedbackApi{

    private final FeedbackService feedbackService;

    //학생 피드백 등록(선생님용)
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<String>> createFeedback(
            @RequestBody FeedbackCreateRequest request,
            Authentication authentication) {

        // JWT에서 선생님의 email(username)을 추출
        String email = authentication.getName();
        feedbackService.createFeedback(email, request);

        return ResponseEntity.ok(ApiResponse.success(SuccessCode.CREATE_SUCCESS, "피드백이 성공적으로 등록되었네!"));
    }

    // 내 자녀의 피드백 목록 조회
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getFeedbacksForParent(
            @PathVariable Long studentId,
            Authentication authentication) {

        String email = authentication.getName();
        List<FeedbackResponse> responses = feedbackService.getFeedbacksForParent(email, studentId);

        return ResponseEntity.ok(ApiResponse.success(SuccessCode.GET_SUCCESS, responses));
    }

    // 해당 선생이 작성한 특정 학생의 모든 피드백 조회
    @GetMapping("/student/{studentId}/all")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getAllFeedbacks(
            @PathVariable Long studentId) {

        List<FeedbackResponse> responses = feedbackService.getFeedbacksForStaff(studentId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.GET_SUCCESS, responses));
    }

    // 피드백 수정(선생님,관리자)
    @PatchMapping("/{feedbackId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> updateFeedback(
            @PathVariable Long feedbackId,
            @RequestBody FeedbackUpdateRequest request,
            Authentication authentication) {

        feedbackService.updateFeedback(authentication.getName(), feedbackId, request);

        return ResponseEntity.ok(ApiResponse.success(SuccessCode.UPDATE_SUCCESS, "피드백 수정 완료"));
    }
}