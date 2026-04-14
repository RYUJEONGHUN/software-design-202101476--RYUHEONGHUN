package SoftwareDesign.demo.domain.parent.repository;

import SoftwareDesign.demo.domain.parent.entity.Parent;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ParentRepository extends JpaRepository<Parent, Long> {
    @Query("select p from Parent p " +
            "join fetch p.children pc " +
            "join fetch pc.student s " +
            "join fetch s.user " +
            "where p.id = :parentId")
    Optional<Parent> findByIdWithChildren(@Param("parentId") Long parentId);
}
