package SoftwareDesign.demo.domain.analytics.repository;

import SoftwareDesign.demo.domain.analytics.entity.ClassSubjectAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClassSubjectAnalyticsRepository extends JpaRepository<ClassSubjectAnalytics, Long> {

    //flushAutomatically = true (밀어내기):
    // * 이 삭제 명령을 내리기 전에, 혹시 자바 메모리에 남아있던 변경 사항들이 있다면 DB에 전부 먼저 반영(flush)해 줍니다.
    //clearAutomatically = true (메모리 청소):
    //DB에서 데이터를 시원하게 지웠으니, 자바 메모리(1차 캐시)에 남아있던 낡은 객체 정보들도 싹 비워(clear)버립니다.
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from ClassSubjectAnalytics a where a.semester = :semester")
    int deleteBySemester(@Param("semester") String semester);

    List<ClassSubjectAnalytics> findAllBySemesterOrderByGradeAscClassNumAscSubjectNameAsc(String semester);

    List<ClassSubjectAnalytics> findAllBySemesterAndGradeOrderByClassNumAscSubjectNameAsc(String semester, int grade);

    List<ClassSubjectAnalytics> findAllBySemesterAndGradeAndClassNumOrderBySubjectNameAsc(
            String semester,
            int grade,
            int classNum
    );
}
