package SoftwareDesign.demo.api.notification.dto;


import SoftwareDesign.demo.domain.attendance.entity.Attendance;
import SoftwareDesign.demo.domain.attendance.entity.AttendanceStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AttendanceEvent {

    private Long studentId;
    private String studentName;
    private LocalDate date;
    private AttendanceStatus status;
    private String note;
    private EventAction action;

    public static AttendanceEvent from(Attendance attendance) {
        return from(attendance, EventAction.UPDATED);
    }

    public static AttendanceEvent from(Attendance attendance, EventAction action) {
        return AttendanceEvent.builder()
                .studentId(attendance.getStudent().getId())
                .studentName(attendance.getStudent().getUser().getName())
                .date(attendance.getDate())
                .status(attendance.getStatus())
                .note(attendance.getNote())
                .action(action)
                .build();
    }
}
