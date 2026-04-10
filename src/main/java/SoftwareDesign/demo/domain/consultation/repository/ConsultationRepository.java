package SoftwareDesign.demo.domain.consultation.repository;

import SoftwareDesign.demo.domain.consultation.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long>,ConsultationRepositoryCustom {
}
