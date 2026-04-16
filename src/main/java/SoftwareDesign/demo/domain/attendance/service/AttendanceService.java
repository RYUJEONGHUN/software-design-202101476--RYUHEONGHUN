package SoftwareDesign.demo.domain.attendance.service;

import SoftwareDesign.demo.api.attendance.dto.AttendanceRequest;
import SoftwareDesign.demo.api.attendance.dto.AttendanceSummaryResponse;
import SoftwareDesign.demo.api.attendance.dto.AttendanceUpdateRequest;
import SoftwareDesign.demo.api.notification.dto.AttendanceEvent;
import SoftwareDesign.demo.api.student.dto.StudentResponse;
import SoftwareDesign.demo.domain.attendance.entity.Attendance;
import SoftwareDesign.demo.domain.attendance.entity.AttendanceStatus;
import SoftwareDesign.demo.domain.attendance.repository.AttendanceRepository;
import SoftwareDesign.demo.domain.common.ErrorCode;
import SoftwareDesign.demo.domain.common.exception.CustomException;
import SoftwareDesign.demo.domain.student.entity.Student;
import SoftwareDesign.demo.domain.student.repository.StudentRepository;
import SoftwareDesign.demo.global.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final RabbitTemplate rabbitTemplate;


    // 단건 오늘 출석 등록
    @Transactional
    public void markAttendance(AttendanceRequest request) {
        LocalDate today = LocalDate.now();
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));

        // 오늘 이미 기록이 있으면 에러
        if (attendanceRepository.existsByStudentAndDate(student, today)) {
            throw new CustomException(ErrorCode.ALREADY_ATTENDANCE_CHECKED); // 이미 체크됨
        }

        Attendance newAttendance = Attendance.builder()
                .student(student)
                .date(today)
                .status(request.getStatus())
                .note(request.getNote())
                .build();
        attendanceRepository.save(newAttendance);

        //  RabbitMQ로 전송 (이름표 붙여서 던지기)
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.COMMON_EXCHANGE,
                RabbitMQConfig.ATTENDANCE_ROUTING_KEY,
                AttendanceEvent.from(newAttendance)
        );
    }

    // 일괄 출석 등록
    @Transactional
    public void markBulkAttendance(List<AttendanceRequest> requests) {
        for (AttendanceRequest request : requests) {
            markAttendance(request); // 위에서 만든 로직을 재사용
        }
    }

    @Transactional
    public void updateAttendanceRecord(Long attendanceId, AttendanceUpdateRequest request) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new CustomException(ErrorCode.ATTENDANCE_NOT_FOUND));

        // 날짜는 그대로 두고 상태와 비고만 수정
        attendance.updateStatus(request.getStatus(), request.getNote());
    }

    @Transactional(readOnly = true)
    public double calculateAttendanceRate(Long studentId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));

        List<Attendance> records = attendanceRepository.findAllByStudentId(studentId);
        if (records.isEmpty()) return 0.0;

        // 결석(ABSENT)을 제외한 나머지를 출석으로 인정하는 간단한 계산.
        long attendedDays = records.stream()
                .filter(a -> a.getStatus() != AttendanceStatus.ABSENT)
                .count();

        return (double) attendedDays / records.size() * 100;
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getUnmarkedStudents(LocalDate date) {
        // 전교생 목록 조회
        List<Student> allStudents = studentRepository.findAll();

        // 해당 날짜에 출석 기록이 있는 학생 ID 추출
        Set<Long> attendedStudentIds = attendanceRepository.findAllByDate(date).stream()
                .map(a -> a.getStudent().getId())
                .collect(Collectors.toSet());

        // 전체 학생 중 출석 기록이 없는 학생들만 필터링
        return allStudents.stream()
                .filter(s -> !attendedStudentIds.contains(s.getId()))
                .map(StudentResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AttendanceSummaryResponse getMonthlyReport(Long studentId, int year, int month) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Attendance> records = attendanceRepository.findAllByStudentIdAndDateBetween(studentId, start, end);

        long present = records.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
        long tardy = records.stream().filter(a -> a.getStatus() == AttendanceStatus.TARDY).count();
        long absent = records.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count();
        long excused = records.stream().filter(a -> a.getStatus() == AttendanceStatus.EXCUSED).count();


        double rate = records.isEmpty() ? 0.0 : (double) (present + excused) / records.size() * 100;

        return new AttendanceSummaryResponse(present, tardy, absent, excused, Math.round(rate * 10) / 10.0);
    }

}