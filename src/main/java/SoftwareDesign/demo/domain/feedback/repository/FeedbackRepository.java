package SoftwareDesign.demo.domain.feedback.repository;

import SoftwareDesign.demo.domain.feedback.entity.Feedback;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback,Long> {
    // 특정 학생의 피드백 중 학부모에게 공개된 것만 조회
    @Query("select f from Feedback f " +
            "join fetch f.teacher t " +
            "join fetch t.user " +
            "where f.student.id = :studentId and f.visibleToParent = true")
    List<Feedback> findByStudentIdAndIsVisibleToParentTrue(@Param("studentId") Long studentId);


    @Query("select f from Feedback f " +
            "join fetch f.teacher t " +
            "join fetch t.user " + // 선생님 이름까지 한방에!
            "where f.student.id = :studentId")
    List<Feedback> findAllByStudentId(@Param("studentId") Long studentId);
}
