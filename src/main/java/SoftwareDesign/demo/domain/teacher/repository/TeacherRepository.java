package SoftwareDesign.demo.domain.teacher.repository;

import SoftwareDesign.demo.domain.student.entity.Student;
import SoftwareDesign.demo.domain.teacher.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    @Query("select t from Teacher t " +
            "join fetch t.user u " +
            "join fetch t.subject s " +
            "where u.username = :email")
    Optional<Teacher> findByUserEmailWithDetails(String email);

    Optional<Teacher> findByUserUsername(String email);
}
