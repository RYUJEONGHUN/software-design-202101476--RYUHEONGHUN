package SoftwareDesign.demo.domain.parent.repository;

import SoftwareDesign.demo.domain.parent.entity.Parent;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {
    @Query("select p from Parent p " +
            "join fetch p.children pc " +
            "join fetch pc.student s " +
            "join fetch s.user " +
            "where p.id = :parentId")
    Optional<Parent> findByIdWithChildren(@Param("parentId") Long parentId);

    @Query("select p from Parent p " +
            "join p.children pc " +
            "where pc.student.id = :studentId")
    Optional<Parent> findByStudentId(@Param("studentId") Long studentId);
}
