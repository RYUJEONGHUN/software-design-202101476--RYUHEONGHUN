package SoftwareDesign.demo.domain.teacher.repository;

import SoftwareDesign.demo.domain.student.entity.Student;
import SoftwareDesign.demo.domain.teacher.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
}
