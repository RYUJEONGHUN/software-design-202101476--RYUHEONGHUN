package SoftwareDesign.demo.api.consultation;

import SoftwareDesign.demo.api.consultation.dto.ConsultationRequest;
import SoftwareDesign.demo.api.consultation.dto.ConsultationResponse;
import SoftwareDesign.demo.api.consultation.dto.ConsultationSearchCondition;
import SoftwareDesign.demo.domain.common.ApiResponse;
import SoftwareDesign.demo.domain.common.ErrorCode;
import SoftwareDesign.demo.domain.common.SuccessCode;
import SoftwareDesign.demo.domain.common.exception.CustomException;
import SoftwareDesign.demo.domain.consultation.service.ConsultationService;
import SoftwareDesign.demo.domain.user.entity.User;
import SoftwareDesign.demo.domain.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/consultations")
@RequiredArgsConstructor
@Tag(name = "Consultation", description = "상담 관리 API")
public class ConsultationController implements ConsultationApi{

    private final ConsultationService consultationService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<Long>> create(
            @RequestBody ConsultationRequest request,
            Authentication authentication) {

        String loginUserEmail = authentication.getName();
        Long id = consultationService.createConsultation(request, loginUserEmail);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.CREATE_SUCCESS, id));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<ConsultationResponse>>> search(
            ConsultationSearchCondition condition) {

        return ResponseEntity.ok(ApiResponse.success(
                SuccessCode.GET_SUCCESS,
                consultationService.searchConsultations(condition)));
    }
}