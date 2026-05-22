package SoftwareDesign.demo.api.analytics;

import SoftwareDesign.demo.api.analytics.dto.ClassSubjectAnalyticsResponse;
import SoftwareDesign.demo.domain.analytics.service.AnalyticsService;
import SoftwareDesign.demo.domain.common.ApiResponse;
import SoftwareDesign.demo.domain.common.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics/olap")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @PostMapping("/refresh")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> refreshClassSubjectAnalytics(@RequestParam String semester) {
        int rowCount = analyticsService.refreshClassSubjectAnalytics(semester);

        return ResponseEntity.ok(ApiResponse.success(
                SuccessCode.UPDATE_SUCCESS,
                "OLAP analysis table refreshed. rows=" + rowCount
        ));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ClassSubjectAnalyticsResponse>>> getClassSubjectAnalytics(
            @RequestParam String semester,
            @RequestParam(required = false) Integer grade,
            @RequestParam(required = false) Integer classNum
    ) {
        List<ClassSubjectAnalyticsResponse> response =
                analyticsService.getClassSubjectAnalytics(semester, grade, classNum);

        return ResponseEntity.ok(ApiResponse.success(SuccessCode.GET_SUCCESS, response));
    }
}
