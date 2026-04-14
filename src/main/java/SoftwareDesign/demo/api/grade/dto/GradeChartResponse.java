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
    private Map<String, Integer> myScores;       // 내 점수
    private Map<String, Double> classAverages;   // 우리 반 평균
    private Map<String, Double> totalAverages;   // 전교 평균

    public static GradeChartResponse of(Student student, String semester,
                                        List<Grade> myGrades,
                                        Map<Long, Double> classAvgMap,
                                        Map<Long, Double> totalAvgMap) {

        Map<String, Integer> myScoreMap = new HashMap<>();
        Map<String, Double> classMap = new HashMap<>();
        Map<String, Double> totalMap = new HashMap<>();

        for (Grade g : myGrades) {
            String subName = g.getSubject().getName();
            Long subId = g.getSubject().getId();

            myScoreMap.put(subName, g.getScore());
            classMap.put(subName, classAvgMap.getOrDefault(subId, 0.0));
            totalMap.put(subName, totalAvgMap.getOrDefault(subId, 0.0));
        }

        return GradeChartResponse.builder()
                .studentName(student.getUser().getName())
                .semester(semester)
                .myScores(myScoreMap)
                .classAverages(classMap)
                .totalAverages(totalMap)
                .build();
    }
}
