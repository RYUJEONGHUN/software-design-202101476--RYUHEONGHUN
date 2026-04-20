package SoftwareDesign.demo.domain.attendance.repository;

import SoftwareDesign.demo.api.attendance.dto.AttendanceCount;
import SoftwareDesign.demo.domain.attendance.entity.Attendance;
import SoftwareDesign.demo.domain.attendance.entity.AttendanceStatus;
import SoftwareDesign.demo.domain.student.entity.Student;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // 특정 학생의 특정 기간(한 달 등) 출석 기록 조회
    List<Attendance> findAllByStudentIdAndDateBetween(Long studentId, LocalDate startDate, LocalDate endDate);

    // 특정 날짜의 전교생 출석 현황 조회
    List<Attendance> findAllByDate(LocalDate date);

    // 특정 학생의 모든 출석 기록
    List<Attendance> findAllByStudentId(Long studentId);

    @Query("SELECT new SoftwareDesign.demo.api.attendance.dto.AttendanceCount(" +
            "COALESCE(SUM(CASE WHEN a.status = 'PRESENT' THEN 1L ELSE 0L END), 0L), " +
            "COALESCE(SUM(CASE WHEN a.status = 'ABSENT' THEN 1L ELSE 0L END), 0L), " +
            "COALESCE(SUM(CASE WHEN a.status = 'TARDY' THEN 1L ELSE 0L END), 0L), " +
            "COALESCE(SUM(CASE WHEN a.status = 'EXCUSED' THEN 1L ELSE 0L END), 0L)) " +
            "FROM Attendance a WHERE a.student.id = :studentId")
    AttendanceCount getTotalCounts(@Param("studentId") Long studentId);


    Boolean existsByStudentAndDate(Student student,LocalDate today);


    @Query("select a.student.studentNumber, count(a) from Attendance a " +
            "where a.student.studentNumber in :studentNumbers and a.status = :status " +
            "group by a.student.studentNumber")
    List<Object[]> countByStudentNumbers(@Param("studentNumbers") List<String> studentNumbers, @Param("status") AttendanceStatus status);
}