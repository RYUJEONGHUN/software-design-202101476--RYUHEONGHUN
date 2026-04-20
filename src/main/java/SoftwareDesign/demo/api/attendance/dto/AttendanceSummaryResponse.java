package SoftwareDesign.demo.api.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class AttendanceSummaryResponse {
    private long presentCount;
    private long tardyCount;
    private long absentCount;
    private long excusedCount;
    private double attendanceRate; // 출석률 (%

    private List<AttendanceRecordDto> records;
}