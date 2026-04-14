package SoftwareDesign.demo.domain.grade.service;

import SoftwareDesign.demo.domain.common.ErrorCode;
import SoftwareDesign.demo.domain.common.exception.CustomException;
import SoftwareDesign.demo.api.grade.dto.GradeChartResponse;
import SoftwareDesign.demo.api.grade.dto.GradeCreateRequest;
import SoftwareDesign.demo.domain.grade.entity.Grade;
import SoftwareDesign.demo.domain.grade.repository.GradeRepository;
import SoftwareDesign.demo.domain.student.entity.Student;
import SoftwareDesign.demo.domain.student.repository.StudentRepository;
import SoftwareDesign.demo.domain.subject.entity.Subject;
import SoftwareDesign.demo.domain.subject.repository.SubjectRepository;
import SoftwareDesign.demo.domain.teacher.entity.Teacher;
import SoftwareDesign.demo.domain.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GradeService {
    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    @Transactional
    public void registerGrade(GradeCreateRequest request,String teacherUsername) {

        //  점수 유효성 체크 (0 ~ 100점 사이인지)
        if (request.getScore() < 0 || request.getScore() > 100) {
            throw new CustomException(ErrorCode.INVALID_SCORE);
        }

        Teacher teacher = teacherRepository.findByUserUsername(teacherUsername)
                .orElseThrow(() -> new CustomException(ErrorCode.TEACHER_NOT_FOUND));

        Subject subject = teacher.getSubject();

        //  학생 및 과목 존재 확인 (기존 로직)
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));


        //  중복 등록 방지 (같은 학기, 같은 과목)
        if (gradeRepository.existsByStudentAndSubjectAndSemester(student, subject, request.getSemester())) {
            throw new CustomException(ErrorCode.ALREADY_GRADE_EXIST);
        }

        // 4. 성적 생성 및 저장
        Grade grade = Grade.builder()
                .student(student)
                .subject(subject)
                .score(request.getScore())
                .semester(request.getSemester())
                .build();

        gradeRepository.save(grade);
    }

    public GradeChartResponse getGradeChart(Long studentId, String semester) {
        // 1. 학생 존재 확인
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        int currentGrade = student.getGrade();
        int currentClass = student.getClassNum();

        // 2. 내 성적 조회 (Fetch Join으로 최적화된 메서드 사용!)
        List<Grade> myGrades = gradeRepository.findAllByStudentIdAndSemesterWithSubject(studentId, semester);
        //if (myGrades.isEmpty()) throw new CustomException(ErrorCode.DATA_NOT_FOUND);

        Map<Long, Double> classAvgMap = convertToMap(
                gradeRepository.findClassAverages(semester, currentGrade, currentClass)
        );

        // 3. 학년 평균 (같은 학년 전체 기준)
        Map<Long, Double> totalAvgMap = convertToMap(
                gradeRepository.findTotalAverages(semester, currentGrade)
        );

        // 4. DTO로 변환하여 반환
        return GradeChartResponse.of(student, semester, myGrades, classAvgMap, totalAvgMap);
    }


    // 중복 로직을 줄이기 위한 헬퍼 메서드
    private Map<Long, Double> convertToMap(List<Object[]> results) {
        return results.stream()
                .collect(Collectors.toMap(
                        obj -> (Long) obj[0], // Subject ID
                        obj -> Math.round((Double) obj[1] * 100) / 100.0 // 소수점 둘째자리
                ));
    }
}
