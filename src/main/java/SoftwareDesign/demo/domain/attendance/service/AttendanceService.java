package SoftwareDesign.demo.domain.attendance.service;

import SoftwareDesign.demo.api.attendance.dto.*;
import SoftwareDesign.demo.api.notification.dto.AttendanceEvent;
import SoftwareDesign.demo.api.student.dto.StudentResponse;
import SoftwareDesign.demo.domain.attendance.entity.Attendance;
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
    public Long markAttendance(AttendanceRequest request) {
        // 요청에 날짜가 있으면 그 날짜로, 없으면 오늘 날짜로 설정!
        LocalDate targetDate = (request.getDate() != null) ? request.getDate() : LocalDate.now();

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));

        // 해당 날짜에 이미 기록이 있으면 에러
        if (attendanceRepository.existsByStudentAndDate(student, targetDate)) {
            throw new CustomException(ErrorCode.ALREADY_ATTENDANCE_CHECKED);
        }

        Attendance newAttendance = Attendance.builder()
                .student(student)
                .date(targetDate)
                .status(request.getStatus())
                .note(request.getNote())
                .build();
        Attendance saved = attendanceRepository.save(newAttendance);

        //  RabbitMQ로 전송 (이름표 붙여서 던지기)
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.COMMON_EXCHANGE,
                RabbitMQConfig.ATTENDANCE_ROUTING_KEY,
                AttendanceEvent.from(newAttendance)
        );

        return saved.getId();
    }

    // 일괄 출석 등록
    @Transactional
    public List<Long> markBulkAttendance(List<AttendanceRequest> requests) {
        return requests.stream()
                .map(this::markAttendance) // 위에서 수정한 메서드 재사용
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateAttendanceRecord(Long attendanceId, AttendanceUpdateRequest request) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new CustomException(ErrorCode.ATTENDANCE_NOT_FOUND));

        // 날짜는 그대로 두고 상태와 비고만 수정
        attendance.updateStatus(request.getStatus(), request.getNote());
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getUnmarkedStudents(LocalDate date) {
        return attendanceRepository.findUnmarkedStudentsByDate(date).stream()
                .map(StudentResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AttendanceSummaryResponse getMonthlyReport(Long studentId, int year, int month) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        // 해당 월의 카운트 정보만 요약해서 가져옴 (통계용)
        AttendanceCount counts = attendanceRepository.getMonthlyCounts(studentId, start, end);

        // 해당 월의 상세 리스트는 따로 가져옴 (리스트용)
        List<Attendance> records = attendanceRepository.findAllByStudentIdAndDateBetween(studentId, start, end);

        return AttendanceSummaryResponse.builder()
                .presentCount(counts.getPresentCount())
                .tardyCount(counts.getTardyCount())
                .absentCount(counts.getAbsentCount())
                .excusedCount(counts.getExcusedCount())
                .attendanceRate(calculateAdvancedRate(counts))
                .records(records.stream().map(AttendanceRecordDto::new).collect(Collectors.toList()))
                .build();
    }

    // 출석률 계산
    public int calculateAdvancedRate(AttendanceCount counts) {
        long present = counts.getPresentCount();
        long absent  = counts.getAbsentCount();
        long tardy   = counts.getTardyCount();
        long excused   = counts.getExcusedCount();

        long totalDays = present + absent + tardy + excused;
        if (totalDays == 0) return 0;

        // 지각, 조퇴도 일단 학교에 '온 것'이므로 분자에 더해줌
        long attendedDays = present + tardy + excused;
        return (int) ((double) attendedDays / totalDays * 100);
    }


    @Transactional(readOnly = true)
    public double calculateAttendanceRate(Long studentId) {
        // 1. DB에서 카운트 4개를 한 방에 긁어옴 (가장 빠름!)
        AttendanceCount counts = attendanceRepository.getTotalCounts(studentId);

        // 2. 미리 만들어둔 계산 로직 재활용
        return (double) calculateAdvancedRate(counts);
    }

}