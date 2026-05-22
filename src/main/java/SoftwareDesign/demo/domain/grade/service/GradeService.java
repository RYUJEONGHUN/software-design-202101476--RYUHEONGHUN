package SoftwareDesign.demo.domain.grade.service;

import SoftwareDesign.demo.api.grade.dto.GradeChartResponse;
import SoftwareDesign.demo.api.grade.dto.GradeCreateRequest;
import SoftwareDesign.demo.api.grade.dto.GradeUpdateRequest;
import SoftwareDesign.demo.api.notification.dto.GradeEvent;
import SoftwareDesign.demo.domain.common.ErrorCode;
import SoftwareDesign.demo.domain.common.exception.CustomException;
import SoftwareDesign.demo.domain.grade.entity.Grade;
import SoftwareDesign.demo.domain.grade.repository.GradeRepository;
import SoftwareDesign.demo.domain.student.entity.Student;
import SoftwareDesign.demo.domain.student.repository.StudentRepository;
import SoftwareDesign.demo.domain.subject.entity.Subject;
import SoftwareDesign.demo.domain.teacher.entity.Teacher;
import SoftwareDesign.demo.domain.teacher.repository.TeacherRepository;
import SoftwareDesign.demo.domain.user.entity.User;
import SoftwareDesign.demo.domain.user.entity.UserRole;
import SoftwareDesign.demo.domain.user.repository.UserRepository;
import SoftwareDesign.demo.global.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public void registerGrade(GradeCreateRequest request, String teacherUsername) {
        if (request.getScore() < 0 || request.getScore() > 100) {
            throw new CustomException(ErrorCode.INVALID_SCORE);
        }

        Teacher teacher = teacherRepository.findByUserUsername(teacherUsername)
                .orElseThrow(() -> new CustomException(ErrorCode.TEACHER_NOT_FOUND));
        Subject subject = teacher.getSubject();

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (gradeRepository.existsByStudentAndSubjectAndSemester(student, subject, request.getSemester())) {
            throw new CustomException(ErrorCode.ALREADY_GRADE_EXIST);
        }

        Grade grade = Grade.builder()
                .student(student)
                .subject(subject)
                .score(request.getScore())
                .semester(request.getSemester())
                .build();

        gradeRepository.save(grade);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.COMMON_EXCHANGE,
                RabbitMQConfig.GRADE_ROUTING_KEY,
                GradeEvent.from(grade)
        );
    }

    public GradeChartResponse getGradeChart(Long studentId, String semester) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        int currentGrade = student.getGrade();
        int currentClass = student.getClassNum();

        List<Grade> myGrades = gradeRepository.findAllByStudentIdAndSemesterWithSubject(studentId, semester);

        Map<Long, Double> classAvgMap = calculateAverageBySubject(
                gradeRepository.findClassGrades(semester, currentGrade, currentClass)
        );

        Map<Long, Double> totalAvgMap = calculateAverageBySubject(
                gradeRepository.findTotalGrades(semester, currentGrade)
        );

        return GradeChartResponse.of(student, semester, myGrades, classAvgMap, totalAvgMap);
    }

    private Map<Long, Double> calculateAverageBySubject(List<Grade> grades) {
        return grades.stream()
                .filter(grade -> grade.getScore() != null)
                .collect(Collectors.groupingBy(
                        grade -> grade.getSubject().getId(),
                        Collectors.averagingInt(Grade::getScore)
                ))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> Math.round(entry.getValue() * 100) / 100.0
                ));
    }

    @Transactional
    public void updateGrade(Long gradeId, GradeUpdateRequest request, String teacherUsername) {
        if (request.getScore() < 0 || request.getScore() > 100) {
            throw new CustomException(ErrorCode.INVALID_SCORE);
        }

        User user = userRepository.findByUsername(teacherUsername)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new CustomException(ErrorCode.GRADE_NOT_FOUND));

        if (user.getRole() == UserRole.TEACHER) {
            Teacher teacher = teacherRepository.findById(user.getId())
                    .orElseThrow(() -> new CustomException(ErrorCode.TEACHER_NOT_FOUND));

            if (!grade.getSubject().getId().equals(teacher.getSubject().getId())) {
                throw new CustomException(ErrorCode.FORBIDDEN);
            }
        } else if (user.getRole() != UserRole.ADMIN) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        grade.updateScore(request.getScore());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.COMMON_EXCHANGE,
                RabbitMQConfig.GRADE_ROUTING_KEY,
                GradeEvent.from(grade)
        );
    }
}
