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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GradeService {
    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    @Transactional
    public void registerGrade(GradeCreateRequest request) {

        // 0. 점수 유효성 체크 (0 ~ 100점 사이인지)
        if (request.getScore() < 0 || request.getScore() > 100) {
            throw new CustomException(ErrorCode.INVALID_SCORE);
        }

        // 1. 학생 존재 확인
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 2. 과목 존재 확인
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new CustomException(ErrorCode.SUBJECT_NOT_FOUND)); // 과목 없음 에러

        // 3. 중복 등록 체크 (동일 학기, 동일 과목)
        if (gradeRepository.existsByStudentAndSubjectAndSemester(student, subject, request.getSemester())) {
            throw new CustomException(ErrorCode.ALREADY_GRADE_EXIST); // 이미 성적이 등록
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

        // 2. 해당 학기 성적 리스트 조회
        List<Grade> grades = gradeRepository.findAllByStudentIdAndSemester(studentId, semester);

        if (grades.isEmpty()) {
            throw new CustomException(ErrorCode.DATA_NOT_FOUND); // 성적 데이터가 없네
        }

        // 3. DTO로 변환하여 반환
        return GradeChartResponse.of(student, semester, grades);
    }
}
