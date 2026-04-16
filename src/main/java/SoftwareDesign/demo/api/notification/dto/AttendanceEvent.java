package SoftwareDesign.demo.api.notification.dto;


import SoftwareDesign.demo.domain.attendance.entity.Attendance;
import SoftwareDesign.demo.domain.attendance.entity.AttendanceStatus;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AttendanceEvent {

    private Long studentId;
    private String studentName;
    private AttendanceStatus status;
    private String note;

    public static AttendanceEvent from(Attendance attendance) {
        return AttendanceEvent.builder()
                .studentId(attendance.getStudent().getId())
                .studentName(attendance.getStudent().getUser().getName())
                .status(attendance.getStatus())
                .note(attendance.getNote())
                .build();
    }
}