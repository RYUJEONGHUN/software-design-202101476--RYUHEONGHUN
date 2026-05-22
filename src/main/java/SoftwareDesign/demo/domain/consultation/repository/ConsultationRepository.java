package SoftwareDesign.demo.domain.consultation.repository;

import SoftwareDesign.demo.domain.consultation.entity.Consultation;
import SoftwareDesign.demo.domain.teacher.entity.Teacher;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long>,ConsultationRepositoryCustom {

    @Query("select c from Consultation c " +
            "where c.id in (select max(c2.id) from Consultation c2 " +
            "               where c2.student.studentNumber in :studentNumbers " +
            "               group by c2.student.studentNumber)")
    List<Consultation> findLatestConsultationsByStudentNumber(@Param("studentNumbers") List<String> studentNumbers);

    long countByTeacherAndNextPlanDateGreaterThanEqual(Teacher teacher, LocalDate date);


    @Query("SELECT c FROM Consultation c " +
            "JOIN FETCH c.student s " +   // Consultation -> Student 페치 조인
            "JOIN FETCH s.user u " +      // Student -> User 페치 조인
            "WHERE c.teacher = :teacher " +
            "AND c.nextPlanDate >= :date " +
            "ORDER BY c.nextPlanDate ASC")
    List<Consultation> findAllByTeacherAndNextPlanDateGreaterThanEqualOrderByNextPlanDateAsc(Teacher teacher, LocalDate date);

    @Query("select c from Consultation c " +
            "join fetch c.student s " +
            "where c.consultationDate between :startDate and :endDate")
    List<Consultation> findAllByConsultationDateBetweenWithStudent(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}
