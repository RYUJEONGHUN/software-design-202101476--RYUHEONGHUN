package SoftwareDesign.demo.domain.consultation.repository;

import SoftwareDesign.demo.domain.consultation.entity.Consultation;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long>,ConsultationRepositoryCustom {

    @Query("select c from Consultation c " +
            "where c.id in (select max(c2.id) from Consultation c2 " +
            "               where c2.student.studentNumber in :studentNumbers " +
            "               group by c2.student.studentNumber)")
    List<Consultation> findLatestConsultationsByStudentNumber(@Param("studentNumbers") List<String> studentNumbers);
}
