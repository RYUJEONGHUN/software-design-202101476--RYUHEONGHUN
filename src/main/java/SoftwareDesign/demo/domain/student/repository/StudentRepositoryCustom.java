package SoftwareDesign.demo.domain.student.repository;

import SoftwareDesign.demo.api.consultation.dto.ConsultationDto;
import SoftwareDesign.demo.api.feedback.dto.FeedbackDto;
import SoftwareDesign.demo.api.grade.dto.SubjectScoreDto;
import SoftwareDesign.demo.api.student.dto.StudentSearchCondition;
import SoftwareDesign.demo.api.student.dto.StudentSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentRepositoryCustom{
    Page<StudentSummaryResponse> search(StudentSearchCondition condition, Pageable pageable);
    List<SubjectScoreDto> findRecentScoresByStudentId(Long studentId);
    List<FeedbackDto> findRecentFeedbacks(Long studentId);
    List<ConsultationDto> findRecentConsultations(Long studentId);
}
