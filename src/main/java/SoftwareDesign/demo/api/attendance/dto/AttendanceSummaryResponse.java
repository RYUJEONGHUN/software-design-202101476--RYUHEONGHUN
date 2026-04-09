package SoftwareDesign.demo.api.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttendanceSummaryResponse {
    private long presentCount;
    private long tardyCount;
    private long absentCount;
    private long excusedCount;
    private double attendanceRate; // 출석률 (%)
}