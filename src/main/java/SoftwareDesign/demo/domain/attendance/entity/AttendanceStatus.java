package SoftwareDesign.demo.domain.attendance.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "출석 상태 (PRESENT: 출석, TARDY: 지각, ABSENT: 결석, EXCUSED: 공결)")
public enum AttendanceStatus {
    PRESENT("출석"),
    TARDY("지각"),
    ABSENT("결석"),
    EXCUSED("공결"); // 병결이나 공식 행사 참여 등

    private final String description;
    AttendanceStatus(String description) { this.description = description; }
}