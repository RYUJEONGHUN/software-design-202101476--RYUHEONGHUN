package SoftwareDesign.demo.api.consultation;


import SoftwareDesign.demo.api.consultation.dto.ConsultationRequest;
import SoftwareDesign.demo.api.consultation.dto.ConsultationResponse;
import SoftwareDesign.demo.api.consultation.dto.ConsultationSearchCondition;
import SoftwareDesign.demo.domain.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Consultation", description = "상담 관리 API")
public interface ConsultationApi {
    @Operation(summary = "상담 기록 등록", description = "선생님이 학생의 상담기록을 입력.")
    public ResponseEntity<ApiResponse<Long>> create(
            @RequestBody ConsultationRequest request,
            Authentication authentication);

    @Operation(summary = "상담 내역 필터링 검색", description = "선생님이 학생의 상담 내역을 픨터링으로 검색.")
    public ResponseEntity<ApiResponse<List<ConsultationResponse>>> search(
            ConsultationSearchCondition condition);

}
