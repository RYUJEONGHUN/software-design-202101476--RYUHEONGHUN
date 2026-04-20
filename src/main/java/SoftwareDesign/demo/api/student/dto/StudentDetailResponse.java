package SoftwareDesign.demo.api.student.dto;

import SoftwareDesign.demo.api.consultation.dto.ConsultationDto;
import SoftwareDesign.demo.api.feedback.dto.FeedbackDto;
import SoftwareDesign.demo.api.grade.dto.SubjectScoreDto;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Builder
@Getter
public class StudentDetailResponse {
    // 1. 기본 인적 사항 (상단 카드)
    private Long id;
    private String name;
    private String studentNumber;
    private Integer grade;
    private Integer classNum;
    private Integer attendanceRate; // 계산된 퍼센트

    // 2. 성적 분석 (중단 레이더 차트용)
    // "국어: 80, 영어: 90..." 처럼 과목별 평균 혹은 최근 점수
    private List<SubjectScoreDto> subjectScores;

    // 3. 최근 피드백 (하단 왼쪽)
    private List<FeedbackDto> recentFeedbacks;

    // 4. 최근 상담 내역 (하단 오른쪽)
    private List<ConsultationDto> recentConsultations;


}