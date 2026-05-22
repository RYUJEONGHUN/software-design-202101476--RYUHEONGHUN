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

    List<Grade> findAllByStudentIdAndSemester(Long studentId, String semester);

    @Query("select g from Grade g " +
            "join fetch g.subject " +
            "join fetch g.student s " +
            "where g.semester = :semester")
    List<Grade> findAllBySemesterWithStudentAndSubject(@Param("semester") String semester);

    @Query("select g from Grade g " +
            "join fetch g.student s " +
            "where s.studentNumber in :studentNumbers")
    List<Grade> findAllByStudentNumbers(@Param("studentNumbers") List<String> studentNumbers);

    @Query("select g from Grade g " +
            "join fetch g.subject " +
            "join fetch g.student s " +
            "join fetch s.user " +
            "where g.student.id = :studentId and g.semester = :semester")
    List<Grade> findAllByStudentIdAndSemesterWithSubject(
            @Param("studentId") Long studentId,
            @Param("semester") String semester
    );

    @Query("select g from Grade g " +
            "join fetch g.subject " +
            "join fetch g.student s " +
            "where g.semester = :semester " +
            "and s.grade = :grade " +
            "and s.classNum = :classNum")
    List<Grade> findClassGrades(
            @Param("semester") String semester,
            @Param("grade") int grade,
            @Param("classNum") int classNum
    );

    @Query("select g from Grade g " +
            "join fetch g.subject " +
            "join fetch g.student s " +
            "where g.semester = :semester " +
            "and s.grade = :grade")
    List<Grade> findTotalGrades(
            @Param("semester") String semester,
            @Param("grade") int grade
    );
}
