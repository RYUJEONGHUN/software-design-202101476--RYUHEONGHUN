package SoftwareDesign.demo.api.grade.dto;


import SoftwareDesign.demo.domain.grade.entity.Grade;
import SoftwareDesign.demo.domain.student.entity.Student;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class GradeChartResponse {
    private String studentName;
    private String semester;
    private Map<String, Integer> scores; // 예: {"국어": 90, "수학": 85}
    private double average;              // 평균 점수

    public static GradeChartResponse of(Student student, String semester, List<Grade> grades) {
        Map<String, Integer> scoreMap = grades.stream()
                .collect(Collectors.toMap(
                        g -> g.getSubject().getName(),
                        Grade::getScore
                ));

        double avg = grades.stream()
                .mapToInt(Grade::getScore)
                .average()
                .orElse(0.0);

        return GradeChartResponse.builder()
                .studentName(student.getUser().getName())
                .semester(semester)
                .scores(scoreMap)
                .average(Math.round(avg * 100) / 100.0) // 소수점 둘째자리까지!
                .build();
    }
}
