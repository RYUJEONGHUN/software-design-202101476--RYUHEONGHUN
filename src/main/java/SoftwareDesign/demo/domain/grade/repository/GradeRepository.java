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

    @Query("select g.subject.name, avg(g.score) from Grade g " +
            "where g.semester = :semester " +
            "group by g.subject.name")
    List<Object[]> findAverageScoresBySemester(@Param("semester") String semester);


    @Query("select g.student.studentNumber, avg(g.score) from Grade g " +
            "where g.student.studentNumber in :studentNumbers " +
            "group by g.student.studentNumber")
    List<Object[]> findAverageScoresByStudentNumbers(@Param("studentNumbers") List<String> studentNumbers);

    @Query("select g from Grade g " +
            "join fetch g.subject " +
            "join fetch g.student s " +
            "join fetch s.user " +
            "where g.student.id = :studentId and g.semester = :semester")
    List<Grade> findAllByStudentIdAndSemesterWithSubject(@Param("studentId") Long studentId, @Param("semester") String semester);

    // 1. 우리 반 평균 (같은 학년, 같은 반, 같은 학기)
    @Query("select g.subject.id, avg(g.score) from Grade g " +
            "where g.semester = :semester " +
            "and g.student.grade = :grade " + // 👈 학년 조건 추가
            "and g.student.classNum = :classNum " +
            "group by g.subject.id")
    List<Object[]> findClassAverages(@Param("semester") String semester,
                                     @Param("grade") int grade,
                                     @Param("classNum") int classNum);

    // 2. 전교(학년) 평균 (같은 학년, 같은 학기)
    @Query("select g.subject.id, avg(g.score) from Grade g " +
            "where g.semester = :semester " +
            "and g.student.grade = :grade " + // 👈 같은 학년 전체 데이터
            "group by g.subject.id")
    List<Object[]> findTotalAverages(@Param("semester") String semester,
                                     @Param("grade") int grade);
}