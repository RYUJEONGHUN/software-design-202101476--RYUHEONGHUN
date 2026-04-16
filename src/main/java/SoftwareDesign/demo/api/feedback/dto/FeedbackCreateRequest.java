package SoftwareDesign.demo.api.feedback.dto;

import SoftwareDesign.demo.domain.feedback.entity.FeedbackCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class FeedbackCreateRequest {
    private Long studentId;           // 대상 학생
    private FeedbackCategory category; // 성적, 행동, 출결, 태도 중 선택
    private String content;// 피드백 내용

    @JsonProperty("visibleToParent")
    private boolean visibleToParent; // 학부모 공개 여부
}
