package SoftwareDesign.demo.domain.grade.repository;

import SoftwareDesign.demo.domain.grade.entity.Grade;
import SoftwareDesign.demo.domain.student.entity.Student;
import SoftwareDesign.demo.domain.subject.entity.Subject;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    boolean existsByStudentAndSubjectAndSemester(Student student, Subject subject, String semester);

    // 특정 학생의 특정 학기 성적 전부 가져오기
    List<Grade> findAllByStudentIdAndSemester(Long studentId, String semester);

    @Query("select g.student.studentNumber, avg(g.score) from Grade g " +
            "where g.student.studentNumber in :studentNumbers " +
            "group by g.student.studentNumber")
    List<Object[]> findAverageScoresByStudentNumbers(@Param("studentNumbers") List<String> studentNumbers);
}