package SoftwareDesign.demo.api.attendance.dto;

import SoftwareDesign.demo.domain.attendance.entity.AttendanceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AttendanceRequest {
    private Long studentId;      // 어떤 학생인지
    @Schema(
            description = "출석 상태",
            allowableValues = {"PRESENT", "TARDY", "ABSENT", "EXCUSED"} //
    )
    private AttendanceStatus status; // 출석/지각/결석/공결 상태
    private String note;         // 특이사항
}