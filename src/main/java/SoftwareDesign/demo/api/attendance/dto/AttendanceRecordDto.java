package SoftwareDesign.demo.api.attendance.dto;

import SoftwareDesign.demo.domain.attendance.entity.Attendance;
import SoftwareDesign.demo.domain.attendance.entity.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class AttendanceRecordDto {
    private Long attendanceId;
    private LocalDate date;
    private AttendanceStatus status;
    private String note;


    public AttendanceRecordDto(Attendance attendance){
        this.attendanceId = attendance.getId();
        this.date = attendance.getDate();
        this.status = attendance.getStatus();
        this.note = attendance.getNote();
    }
}