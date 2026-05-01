package SoftwareDesign.demo.domain.teacher.service;

import SoftwareDesign.demo.api.teacher.dto.ScheduledConsultationDto;
import SoftwareDesign.demo.api.teacher.dto.TeacherDashboardResponse;
import SoftwareDesign.demo.domain.attendance.repository.AttendanceRepository;
import SoftwareDesign.demo.domain.attendance.service.AttendanceService;
import SoftwareDesign.demo.domain.common.ErrorCode;
import SoftwareDesign.demo.domain.common.exception.CustomException;
import SoftwareDesign.demo.domain.consultation.entity.Consultation;
import SoftwareDesign.demo.domain.consultation.repository.ConsultationRepository;
import SoftwareDesign.demo.domain.subject.entity.Subject;
import SoftwareDesign.demo.domain.subject.repository.SubjectRepository;
import SoftwareDesign.demo.domain.teacher.entity.Teacher;
import SoftwareDesign.demo.domain.teacher.repository.TeacherRepository;
import SoftwareDesign.demo.domain.user.entity.User;
import SoftwareDesign.demo.domain.user.entity.UserRole;
import SoftwareDesign.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final ConsultationRepository consultationRepository;
    private final StringRedisTemplate redisTemplate;
    private final AttendanceRepository attendanceRepository;


    @Transactional
    public void registerTeacher(Long userId, String subjectName) { // 또는 subjectId
        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 과목 조회
        Subject subject = subjectRepository.findByName(subjectName)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBJECT_NOT_FOUND));

        // 이미 선생으로 등록된 유저인지 체크하는 방어 로직
        if (teacherRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_TEACHER); // 이미 등록된 선생
        }
        // 권한 변경
        user.updateRole(UserRole.TEACHER);

        // 교사 엔티티 생성 및 저장 (Subject 객체를 직접 연결!)
        Teacher teacher = Teacher.builder()
                .user(user)
                .subject(subject) // 이제 String이 아니라 Subject 객체가 들어간다네!
                .teacherIdNum("T-" + System.currentTimeMillis())
                .build();

        teacherRepository.save(teacher);
    }

    @Transactional(readOnly = true)
    public TeacherDashboardResponse getTeacherDashboard(String email) {
        Teacher teacher = teacherRepository.findByUserEmailWithDetails(email)
                .orElseThrow(() -> new CustomException(ErrorCode.TEACHER_NOT_FOUND));

        // Redis에서 카운트 가져오기 (없으면 DB 조회)
        //long unmarkedCount = getUnmarkedCountFromRedis(LocalDate.now());
        long consultationCount = consultationRepository.countByTeacherAndNextPlanDateGreaterThanEqual(teacher, LocalDate.now());

        return TeacherDashboardResponse.builder()
                .name(teacher.getUser().getName()) // u.name 필드 접근
                .role(teacher.getUser().getRole().name())
                .teacherIdNum(teacher.getTeacherIdNum())
                .subjectName(teacher.getSubject().getName()) // s.name 필드 접근
                .scheduledConsultationCount(consultationCount)
                //.unreadNotificationCount(unmarkedCount)
                .build();
    }

    private long getUnmarkedCountFromRedis(LocalDate date) {
        String key = "attendance:unmarked:count:" + date;
        String val = redisTemplate.opsForValue().get(key);

        if (val == null) {
            // Redis에 없으면 DB에서 COUNT해서 다시 세팅
            long dbCount = attendanceRepository.countUnmarkedStudentsByDate(date);
            redisTemplate.opsForValue().set(key, String.valueOf(dbCount));
            return dbCount;
        }
        return Long.parseLong(val);
    }

    @Transactional(readOnly = true)
    public List<ScheduledConsultationDto> getScheduledConsultations(String email) {
        // 선생님 정보 조회
        Teacher teacher = teacherRepository.findByUserEmailWithDetails(email)
                .orElseThrow(() -> new CustomException(ErrorCode.TEACHER_NOT_FOUND));

        // 오늘 이후의 상담 목록 조회 (Repository 활용)
        List<Consultation> consultations = consultationRepository
                .findAllByTeacherAndNextPlanDateGreaterThanEqualOrderByNextPlanDateAsc(teacher, LocalDate.now());

        // Dto로 변환해서 반환
        return consultations.stream()
                .map(consultation -> ScheduledConsultationDto.builder()
                        .studentName(consultation.getStudent().getUser().getName()) // 학생 이름
                        .nextPlanDate(consultation.getNextPlanDate())              // 상담 예정일
                        .build())
                .collect(Collectors.toList());
    }
}
