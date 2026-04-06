package SoftwareDesign.demo.domain.attendance.entity;

import SoftwareDesign.demo.domain.common.BaseTimeEntity;
import SoftwareDesign.demo.domain.student.entity.Student;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attendance extends BaseTimeEntity { // 생성일(created_at) 활용을 위해!

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private LocalDate attendanceDate; // 출석 날짜 (시간 제외)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;

    private String note; // 사유 (예: 병결, 체험학습 등)

    @Builder
    public Attendance(Student student, LocalDate attendanceDate, AttendanceStatus status, String note) {
        this.student = student;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.note = note;
    }
}