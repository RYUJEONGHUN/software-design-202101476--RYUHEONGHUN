package SoftwareDesign.demo.domain.attendance.service;

import SoftwareDesign.demo.api.attendance.dto.*;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Duration;
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
    private final StringRedisTemplate redisTemplate;


    // 단건 오늘 출석 등록
    @Transactional
    public Long markAttendance(AttendanceRequest request) {
        // 요청에 날짜가 있으면 그 날짜로, 없으면 오늘 날짜로 설정!
        LocalDate targetDate = (request.getDate() != null) ? request.getDate() : LocalDate.now();

        // 유일한 키 생성
        String lockKey = "lock:attendance:" + request.getStudentId() + ":" + targetDate;

        // SETNX (Set if Not Exists) 사용: 5초 동안만 유지되는 임시 락
        Boolean isFirstRequest = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "p", Duration.ofSeconds(10));

        if (Boolean.FALSE.equals(isFirstRequest)) {
            throw new CustomException(ErrorCode.ALREADY_ATTENDANCE_CHECKED); // 이미 처리 중이거나 완료된 요청입니다
        }

        try {
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

            // [핵심] 트랜잭션이 성공적으로 커밋된 후에만 Redis 연산 및 메시지 전송 실행
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    updateUnmarkedCount(targetDate, -1);
                    cacheAttendanceRate(request.getStudentId());

                    rabbitTemplate.convertAndSend(
                            RabbitMQConfig.COMMON_EXCHANGE,
                            RabbitMQConfig.ATTENDANCE_ROUTING_KEY,
                            AttendanceEvent.from(saved)
                    );
                }
            });

            return saved.getId();

        } finally {
            redisTemplate.delete(lockKey); // 필요에 따라 선택하게나!
        }

    }

    public void updateUnmarkedCount(LocalDate date, int delta) {
        String key = "attendance:unmarked:count:" + date;

        // 키가 없으면 DB에서 먼저 채우고 Increment (Cache Aside)
        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            long dbCount = attendanceRepository.countUnmarkedStudentsByDate(date);
            redisTemplate.opsForValue().set(key, String.valueOf(dbCount), Duration.ofDays(1));
        }

        // 음수가 되지 않도록 방어 로직 (간단하게는 increment 후 결과가 음수면 0으로 세팅)
        Long result = redisTemplate.opsForValue().increment(key, delta);
        if (result != null && result < 0) {
            redisTemplate.opsForValue().set(key, "0");
        }
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

        // 기존 상태 저장 (출석률 변화 확인용)
        AttendanceStatus oldStatus = attendance.getStatus();

        // 날짜는 그대로 두고 상태와 비고만 수정
        attendance.updateStatus(request.getStatus(), request.getNote());

        // 상태가 변했다면 출석률 캐시 갱신
        if (!oldStatus.equals(request.getStatus())) {
            cacheAttendanceRate(attendance.getStudent().getId());
        }
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

        // 해당 월의 카운트 정보만 요약해서 가져옴 (통계용), 단순한 DB에서 가져오기
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
        // DB에서 카운트 4개를 한 방에 긁어옴
        AttendanceCount counts = attendanceRepository.getTotalCounts(studentId);

        // 미리 만들어둔 계산 로직 재활용
        return (double) calculateAdvancedRate(counts);
    }

    // 학생 출석률 계산
    public double getAttendanceRate(Long studentId) {
        String key = "student:rate:" + studentId;
        String cachedRate = redisTemplate.opsForValue().get(key);

        // 캐시에 데이터가 있다면 바로 반환
        if (cachedRate != null) {
            return Double.parseDouble(cachedRate);
        }

        // 캐시에 없다면? DB에서 계산해서 가져오기 (Cache Aside)
        double dbRate = calculateAttendanceRate(studentId);

        // 다음 조회를 위해 Redis에 다시 채워넣기
        redisTemplate.opsForValue().set(key, String.valueOf(dbRate), Duration.ofDays(1));

        return dbRate;
    }

    public void cacheAttendanceRate(Long studentId) {
        //  기존 DB 통계 로직 호출
        double rate = calculateAttendanceRate(studentId);

        //  Redis에 저장
        String key = "student:rate:" + studentId;
        redisTemplate.opsForValue().set(key, String.valueOf(rate), Duration.ofDays(1));
    }


}