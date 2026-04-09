package SoftwareDesign.demo.api.attendance.dto;

import SoftwareDesign.demo.domain.attendance.entity.AttendanceStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AttendanceUpdateRequest {
    private AttendanceStatus status; // 출석/지각/결석/공결 상태
    private String note;
}
