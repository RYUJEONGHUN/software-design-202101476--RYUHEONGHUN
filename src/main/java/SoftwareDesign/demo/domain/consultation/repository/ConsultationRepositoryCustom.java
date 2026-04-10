package SoftwareDesign.demo.domain.consultation.repository;

import SoftwareDesign.demo.api.consultation.dto.ConsultationSearchCondition;
import SoftwareDesign.demo.domain.consultation.entity.Consultation;

import java.util.List;

public interface ConsultationRepositoryCustom {
    List<Consultation> search(ConsultationSearchCondition condition);
}