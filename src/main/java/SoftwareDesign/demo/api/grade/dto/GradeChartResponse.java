package SoftwareDesign.demo.api.grade.dto;


import SoftwareDesign.demo.domain.grade.entity.Grade;
import SoftwareDesign.demo.domain.student.entity.Student;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class GradeChartResponse {
    private String studentName;
    private String semester;
    private int totalScore;
    private double averageScore;
    private String overallGrade;

    private List<GradeScoreDto> scores;

    public static GradeChartResponse of(Student student, String semester,
                                        List<Grade> myGrades,
                                        Map<Long, Double> classAvgMap,
                                        Map<Long, Double> totalAvgMap) {

        int totalScore = myGrades.stream()
                .mapToInt(Grade::getScore)
                .sum();

        double averageScore = myGrades.isEmpty()
                ? 0.0
                : Math.round(((double) totalScore / myGrades.size()) * 100) / 100.0;

        List<GradeScoreDto> scores = myGrades.stream()
                .map(grade -> GradeScoreDto.from(
                        grade,
                        classAvgMap.getOrDefault(grade.getSubject().getId(), 0.0),
                        totalAvgMap.getOrDefault(grade.getSubject().getId(), 0.0)
                ))
                .toList();

        return GradeChartResponse.builder()
                .studentName(student.getUser().getName())
                .semester(semester)
                .totalScore(totalScore)
                .averageScore(averageScore)
                .overallGrade(calculateOverallGrade(averageScore))
                .scores(scores)
                .build();
    }

    private static String calculateOverallGrade(double averageScore) {
        if (averageScore >= 90) return "A";
        if (averageScore >= 80) return "B";
        if (averageScore >= 70) return "C";
        if (averageScore >= 60) return "D";
        return "F";
    }
}
