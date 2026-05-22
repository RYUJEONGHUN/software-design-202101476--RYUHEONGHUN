package SoftwareDesign.demo.domain.analytics.repository;

import SoftwareDesign.demo.domain.analytics.entity.ClassSubjectAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassSubjectAnalyticsRepository extends JpaRepository<ClassSubjectAnalytics, Long> {

    void deleteAllBySemester(String semester);

    List<ClassSubjectAnalytics> findAllBySemesterOrderByGradeAscClassNumAscSubjectNameAsc(String semester);

    List<ClassSubjectAnalytics> findAllBySemesterAndGradeOrderByClassNumAscSubjectNameAsc(String semester, int grade);

    List<ClassSubjectAnalytics> findAllBySemesterAndGradeAndClassNumOrderBySubjectNameAsc(
            String semester,
            int grade,
            int classNum
    );
}
