package SoftwareDesign.demo.api.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttendanceCount {
    private long presentCount;
    private long absentCount;
    private long tardyCount;
    private long excusedCount;

    public AttendanceCount(Long present, Long absent, Long tardy, Long excused) {
        // 여기서 null 체크를 해주는 게 안전하네!
        this.presentCount = (present != null) ? present : 0L;
        this.absentCount = (absent != null) ? absent : 0L;
        this.tardyCount = (tardy != null) ? tardy : 0L;
        this.excusedCount = (excused != null) ? excused : 0L;
    }
}
