package SoftwareDesign.demo.api.feedback;

import SoftwareDesign.demo.api.admin.dto.StudentCreateRequest;
import SoftwareDesign.demo.api.admin.dto.UserRoleUpdateRequest;
import SoftwareDesign.demo.api.feedback.dto.FeedbackCreateRequest;
import SoftwareDesign.demo.api.feedback.dto.FeedbackResponse;
import SoftwareDesign.demo.api.feedback.dto.FeedbackUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Feedback", description = "피드백 작성 및 학부모가 자녀 피드백 조회")
public interface FeedbackApi {
    @Operation(summary = "피드백 작성", description = "선생님이 학생의 피드백을 작성")
    @ApiResponse(responseCode = "200", description = "피드백 생성 완료")
    ResponseEntity<SoftwareDesign.demo.domain.common.ApiResponse<String>> createFeedback(
            @RequestBody FeedbackCreateRequest request,
            Authentication authentication);

    @Operation(summary = "자녀 피드백 조회", description = "학부모가 자녀의 피드백을 조회할 수 있다")
    @ApiResponse(responseCode = "200", description = "자녀 피드백 조회 완료")
    ResponseEntity<SoftwareDesign.demo.domain.common.ApiResponse<List<FeedbackResponse>>> getFeedbacksForParent(
            @PathVariable Long studentId,
            Authentication authentication);

    @Operation(summary = "해당 학생 피드백 조회", description = "해당 학생의 모든 피드백을 조회한다")
    @ApiResponse(responseCode = "200", description = "해당 학생 피드백 조회 완료")
    ResponseEntity<SoftwareDesign.demo.domain.common.ApiResponse<List<FeedbackResponse>>> getAllFeedbacks(
            @PathVariable Long studentId) ;

    @Operation(summary = "피드백 수정", description = "선생과 관리자가 해당 피드백을 수정 할 수 있다")
    @ApiResponse(responseCode = "200", description = "피드백 수정 완료")
    ResponseEntity<SoftwareDesign.demo.domain.common.ApiResponse<String>> updateFeedback(
            @PathVariable Long feedbackId,
            @RequestBody FeedbackUpdateRequest request,
            Authentication authentication);

}
