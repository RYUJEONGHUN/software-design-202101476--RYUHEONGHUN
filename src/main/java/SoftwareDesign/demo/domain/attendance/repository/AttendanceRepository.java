package SoftwareDesign.demo.domain.attendance.repository;

import SoftwareDesign.demo.domain.attendance.entity.Attendance;
import SoftwareDesign.demo.domain.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
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


    Boolean existsByStudentAndDate(Student student,LocalDate today);

}