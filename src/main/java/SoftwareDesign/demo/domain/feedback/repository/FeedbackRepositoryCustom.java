package SoftwareDesign.demo.domain.feedback.repository;

import SoftwareDesign.demo.api.feedback.dto.FeedbackSearchCondition;
import SoftwareDesign.demo.domain.feedback.entity.Feedback;

import java.util.List;

public interface FeedbackRepositoryCustom {
    List<Feedback> searchForParent(Long studentId, FeedbackSearchCondition condition);
    List<Feedback> searchForStaff(Long studentId, FeedbackSearchCondition condition);
}
