package SoftwareDesign.demo.api.analytics.dto;

import SoftwareDesign.demo.domain.analytics.entity.ClassSubjectAnalytics;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClassSubjectAnalyticsResponse {

    private Long id;
    private String semester;
    private int grade;
    private int classNum;
    private Long subjectId;
    private String subjectName;
    private long studentCount;
    private long gradeCount;
    private double averageScore;
    private long presentCount;
    private long absentCount;
    private long tardyCount;
    private long excusedCount;
    private long consultationCount;
    private long feedbackCount;

    public static ClassSubjectAnalyticsResponse from(ClassSubjectAnalytics analytics) {
        return ClassSubjectAnalyticsResponse.builder()
                .id(analytics.getId())
                .semester(analytics.getSemester())
                .grade(analytics.getGrade())
                .classNum(analytics.getClassNum())
                .subjectId(analytics.getSubjectId())
                .subjectName(analytics.getSubjectName())
                .studentCount(analytics.getStudentCount())
                .gradeCount(analytics.getGradeCount())
                .averageScore(analytics.getAverageScore())
                .presentCount(analytics.getPresentCount())
                .absentCount(analytics.getAbsentCount())
                .tardyCount(analytics.getTardyCount())
                .excusedCount(analytics.getExcusedCount())
                .consultationCount(analytics.getConsultationCount())
                .feedbackCount(analytics.getFeedbackCount())
                .build();
    }
}
