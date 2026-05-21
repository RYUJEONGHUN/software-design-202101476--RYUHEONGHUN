package SoftwareDesign.demo.domain.studentrecord.repository;

import SoftwareDesign.demo.domain.studentrecord.entity.StudentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRecordRepository extends JpaRepository<StudentRecord, Long> {
    Optional<StudentRecord> findByStudentIdAndSchoolYearAndSemester(
            Long studentId,
            int schoolYear,
            String semester
    );

    List<StudentRecord> findAllByStudentIdOrderBySchoolYearDescSemesterDesc(Long studentId);
}