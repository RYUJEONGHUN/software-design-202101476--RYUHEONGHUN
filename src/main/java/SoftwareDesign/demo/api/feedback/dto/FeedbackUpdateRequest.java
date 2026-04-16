package SoftwareDesign.demo.api.feedback.dto;

import SoftwareDesign.demo.domain.feedback.entity.FeedbackCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedbackUpdateRequest {
    private FeedbackCategory category; // 성적, 행동, 출결, 태도 중 선택
    private String content;           // 피드백 내용
    private boolean visibleToParent; // 학부모 공개 여부
}
