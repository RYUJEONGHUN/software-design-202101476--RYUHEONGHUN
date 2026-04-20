package SoftwareDesign.demo.domain.student.service;

import SoftwareDesign.demo.api.admin.dto.StudentCreateRequest;
import SoftwareDesign.demo.api.attendance.dto.AttendanceCount;
import SoftwareDesign.demo.api.consultation.dto.ConsultationDto;
import SoftwareDesign.demo.api.feedback.dto.FeedbackDto;
import SoftwareDesign.demo.api.grade.dto.SubjectScoreDto;
import SoftwareDesign.demo.api.student.dto.StudentDetailResponse;
import SoftwareDesign.demo.api.student.dto.StudentSearchCondition;
import SoftwareDesign.demo.api.student.dto.StudentSummaryResponse;
import SoftwareDesign.demo.domain.attendance.entity.AttendanceStatus;
import SoftwareDesign.demo.domain.attendance.repository.AttendanceRepository;
import SoftwareDesign.demo.domain.attendance.service.AttendanceService;
import SoftwareDesign.demo.domain.common.ErrorCode;
import SoftwareDesign.demo.domain.common.exception.CustomException;
import SoftwareDesign.demo.domain.consultation.entity.Consultation;
import SoftwareDesign.demo.domain.consultation.repository.ConsultationRepository;
import SoftwareDesign.demo.domain.grade.repository.GradeRepository;
import SoftwareDesign.demo.domain.student.entity.Student;
import SoftwareDesign.demo.domain.student.repository.StudentRepository;
import SoftwareDesign.demo.domain.user.entity.User;
import SoftwareDesign.demo.domain.user.entity.UserRole;
import SoftwareDesign.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final ConsultationRepository consultationRepository;
    private final AttendanceRepository attendanceRepository;
    private final GradeRepository gradeRepository;
    private final AttendanceService attendanceService;

    @Transactional //
    public void registerStudent(Long userId, StudentCreateRequest request) {
        // 1. 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 이미 학생으로 등록된 유저인지 체크하는 방어 로직이 있으면 더 좋네!
        if (studentRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_STUDENT); // 이미 등록된 학생
        }

        user.updateRole(UserRole.STUDENT);

        // 3. 학생 상세 정보 저장
        Student student = Student.builder()
                .user(user)
                .studentNumber(request.getStudentNumber())
                .grade(request.getGrade())
                .classNum(request.getClassNum())
                .number(request.getNumber())
                .build();

        studentRepository.save(student);

        // 만약 여기서 에러가 나면? 권한 변경도 자동으로 취소(Rollback)된다네!
    }

    @Transactional(readOnly = true)
    public StudentDetailResponse getStudentProfile(Long studentId) {
        // 학생 기본 정보 조회
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));

        // 출석률 계산
        AttendanceCount counts = attendanceRepository.getTotalCounts(studentId);
        long present = counts.getPresentCount();
        long absent  = counts.getAbsentCount();
        long tardy   = counts.getTardyCount();
        long excused   = counts.getExcusedCount();

        int attendanceRate = attendanceService.calculateAdvancedRate(present, absent, tardy, excused);

        //  성적 데이터 조회 (레이더 차트용 과목별 평균 등)
        List<SubjectScoreDto> scores = studentRepository.findRecentScoresByStudentId(studentId);
        List<FeedbackDto> feedbacks = studentRepository.findRecentFeedbacks(studentId);
        List<ConsultationDto> consultations = studentRepository.findRecentConsultations(studentId);

        // 6. 최종 DTO 조립
        return StudentDetailResponse.builder()
                .id(studentId)
                .name(student.getUser().getName())
                .studentNumber(student.getStudentNumber())
                .grade(student.getGrade())
                .classNum(student.getClassNum())
                .attendanceRate(attendanceRate)
                .subjectScores(scores)
                .recentFeedbacks(feedbacks)
                .recentConsultations(consultations)
                .build();
    }

    // 학생 통합 검색 및 조회 (기본 정보 리스트)
    public Page<StudentSummaryResponse> searchStudents(StudentSearchCondition condition, Pageable pageable) {

        // 1. 기본 정보 조회
        Page<StudentSummaryResponse> pageResult = studentRepository.search(condition, pageable);
        List<StudentSummaryResponse> dtos = pageResult.getContent();

        if (dtos.isEmpty()) return pageResult;

        // 2. 학번 리스트 추출
        List<String> sNums = dtos.stream().map(StudentSummaryResponse::getStudentNumber).toList();

        // 3. 통계 데이터 한꺼번에 긁어오기 (In 절 쿼리)
        // Map<학번, 출석횟수> 형태로 변환
        Map<String, Long> attendanceMap = attendanceRepository.countByStudentNumbers(sNums, AttendanceStatus.PRESENT)
                .stream().collect(Collectors.toMap(obj -> (String)obj[0], obj -> (Long)obj[1]));

        Map<String, Long> absenceMap = attendanceRepository.countByStudentNumbers(sNums, AttendanceStatus.ABSENT)
                .stream().collect(Collectors.toMap(obj -> (String)obj[0], obj -> (Long)obj[1]));

        Map<String, Double> gradeMap = gradeRepository.findAverageScoresByStudentNumbers(sNums)
                .stream().collect(Collectors.toMap(obj -> (String)obj[0], obj -> (Double)obj[1]));

        Map<String, String> consultationMap = consultationRepository.findLatestConsultationsByStudentNumber(sNums)
                .stream().collect(Collectors.toMap(
                        c -> c.getStudent().getStudentNumber(),
                        Consultation::getContent
                ));

        // 4. DTO에 데이터 꽂아주기
        for (StudentSummaryResponse dto : dtos) {
            String sn = dto.getStudentNumber();
            dto.setAttendanceCount(attendanceMap.getOrDefault(sn, 0L));
            dto.setAbsenceCount(absenceMap.getOrDefault(sn, 0L));
            dto.setAverageScore(gradeMap.getOrDefault(sn, 0.0));
            dto.setLastConsultationContent(consultationMap.getOrDefault(sn,"상담 내역 없음"));
        }

        return pageResult;
    }

}