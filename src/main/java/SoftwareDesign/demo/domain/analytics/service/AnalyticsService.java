package SoftwareDesign.demo.domain.analytics.service;

import SoftwareDesign.demo.api.analytics.dto.ClassSubjectAnalyticsResponse;
import SoftwareDesign.demo.domain.analytics.entity.ClassSubjectAnalytics;
import SoftwareDesign.demo.domain.analytics.repository.ClassSubjectAnalyticsRepository;
import SoftwareDesign.demo.domain.attendance.entity.Attendance;
import SoftwareDesign.demo.domain.attendance.entity.AttendanceStatus;
import SoftwareDesign.demo.domain.attendance.repository.AttendanceRepository;
import SoftwareDesign.demo.domain.consultation.entity.Consultation;
import SoftwareDesign.demo.domain.consultation.repository.ConsultationRepository;
import SoftwareDesign.demo.domain.feedback.entity.Feedback;
import SoftwareDesign.demo.domain.feedback.repository.FeedbackRepository;
import SoftwareDesign.demo.domain.grade.entity.Grade;
import SoftwareDesign.demo.domain.grade.repository.GradeRepository;
import SoftwareDesign.demo.domain.student.entity.Student;
import SoftwareDesign.demo.domain.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private final ClassSubjectAnalyticsRepository analyticsRepository;
    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final ConsultationRepository consultationRepository;
    private final FeedbackRepository feedbackRepository;

    @Transactional
    public synchronized int refreshClassSubjectAnalytics(String semester) {
        SemesterPeriod period = SemesterPeriod.from(semester);
        List<Student> students = studentRepository.findAll();
        List<Grade> grades = gradeRepository.findAllBySemesterWithStudentAndSubject(semester);
        List<Attendance> attendances = attendanceRepository.findAllByDateBetweenWithStudent(
                period.startDate(),
                period.endDate()
        );
        List<Consultation> consultations = consultationRepository.findAllByConsultationDateBetweenWithStudent(
                period.startDate(),
                period.endDate()
        );
        List<Feedback> feedbacks = feedbackRepository.findAllByCreatedAtBetweenWithStudent(
                period.startDate().atStartOfDay(),
                period.endDate().atTime(23, 59, 59)
        );

        Map<ClassKey, Long> studentCountMap = students.stream()
                .collect(Collectors.groupingBy(
                        student -> new ClassKey(student.getGrade(), student.getClassNum()),
                        Collectors.counting()
                ));

        Map<ClassKey, AttendanceSummary> attendanceSummaryMap = attendances.stream()
                .collect(Collectors.groupingBy(
                        attendance -> new ClassKey(
                                attendance.getStudent().getGrade(),
                                attendance.getStudent().getClassNum()
                        ),
                        Collectors.collectingAndThen(Collectors.toList(), AttendanceSummary::from)
                ));

        Map<ClassKey, Long> consultationCountMap = consultations.stream()
                .collect(Collectors.groupingBy(
                        consultation -> new ClassKey(
                                consultation.getStudent().getGrade(),
                                consultation.getStudent().getClassNum()
                        ),
                        Collectors.counting()
                ));

        Map<ClassKey, Long> feedbackCountMap = feedbacks.stream()
                .collect(Collectors.groupingBy(
                        feedback -> new ClassKey(
                                feedback.getStudent().getGrade(),
                                feedback.getStudent().getClassNum()
                        ),
                        Collectors.counting()
                ));

        Map<AnalyticsKey, List<Grade>> gradeGroups = grades.stream()
                .collect(Collectors.groupingBy(grade -> new AnalyticsKey(
                        grade.getStudent().getGrade(),
                        grade.getStudent().getClassNum(),
                        grade.getSubject().getId(),
                        grade.getSubject().getName()
                )));

        List<ClassSubjectAnalytics> analyticsRows = gradeGroups.entrySet().stream()
                .map(entry -> toAnalytics(semester, entry.getKey(), entry.getValue(),
                        studentCountMap, attendanceSummaryMap, consultationCountMap, feedbackCountMap))
                .toList();

        analyticsRepository.deleteBySemester(semester);
        analyticsRepository.saveAll(analyticsRows);
        return analyticsRows.size();
    }

    public List<ClassSubjectAnalyticsResponse> getClassSubjectAnalytics(String semester, Integer grade, Integer classNum) {
        List<ClassSubjectAnalytics> analytics;

        if (grade != null && classNum != null) {
            analytics = analyticsRepository.findAllBySemesterAndGradeAndClassNumOrderBySubjectNameAsc(
                    semester,
                    grade,
                    classNum
            );
        } else if (grade != null) {
            analytics = analyticsRepository.findAllBySemesterAndGradeOrderByClassNumAscSubjectNameAsc(semester, grade);
        } else {
            analytics = analyticsRepository.findAllBySemesterOrderByGradeAscClassNumAscSubjectNameAsc(semester);
        }

        return analytics.stream()
                .map(ClassSubjectAnalyticsResponse::from)
                .toList();
    }

    private ClassSubjectAnalytics toAnalytics(String semester, AnalyticsKey key, List<Grade> grades,
                                              Map<ClassKey, Long> studentCountMap,
                                              Map<ClassKey, AttendanceSummary> attendanceSummaryMap,
                                              Map<ClassKey, Long> consultationCountMap,
                                              Map<ClassKey, Long> feedbackCountMap) {
        ClassKey classKey = new ClassKey(key.grade(), key.classNum());
        AttendanceSummary attendance = attendanceSummaryMap.getOrDefault(classKey, AttendanceSummary.empty());
        double averageScore = grades.stream()
                .map(Grade::getScore)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        return ClassSubjectAnalytics.builder()
                .semester(semester)
                .grade(key.grade())
                .classNum(key.classNum())
                .subjectId(key.subjectId())
                .subjectName(key.subjectName())
                .studentCount(studentCountMap.getOrDefault(classKey, 0L))
                .gradeCount(grades.size())
                .averageScore(roundTwoDecimal(averageScore))
                .presentCount(attendance.presentCount())
                .absentCount(attendance.absentCount())
                .tardyCount(attendance.tardyCount())
                .excusedCount(attendance.excusedCount())
                .consultationCount(consultationCountMap.getOrDefault(classKey, 0L))
                .feedbackCount(feedbackCountMap.getOrDefault(classKey, 0L))
                .build();
    }

    private double roundTwoDecimal(double value) {
        return Math.round(value * 100) / 100.0;
    }

    private record AnalyticsKey(int grade, int classNum, Long subjectId, String subjectName) {
    }

    private record ClassKey(int grade, int classNum) {
    }

    private record SemesterPeriod(LocalDate startDate, LocalDate endDate) {
        private static SemesterPeriod from(String semester) {
            String[] parts = semester.split("-");
            int year = Integer.parseInt(parts[0]);
            int term = Integer.parseInt(parts[1]);

            if (term == 1) {
                return new SemesterPeriod(LocalDate.of(year, 3, 1), LocalDate.of(year, 8, 31));
            }
            return new SemesterPeriod(LocalDate.of(year, 9, 1), LocalDate.of(year + 1, 2, 28));
        }
    }

    private record AttendanceSummary(long presentCount, long absentCount, long tardyCount, long excusedCount) {
        private static AttendanceSummary from(List<Attendance> attendances) {
            long present = countByStatus(attendances, AttendanceStatus.PRESENT);
            long absent = countByStatus(attendances, AttendanceStatus.ABSENT);
            long tardy = countByStatus(attendances, AttendanceStatus.TARDY);
            long excused = countByStatus(attendances, AttendanceStatus.EXCUSED);
            return new AttendanceSummary(present, absent, tardy, excused);
        }

        private static AttendanceSummary empty() {
            return new AttendanceSummary(0, 0, 0, 0);
        }

        private static long countByStatus(List<Attendance> attendances, AttendanceStatus status) {
            return attendances.stream()
                    .filter(attendance -> attendance.getStatus() == status)
                    .count();
        }
    }
}
