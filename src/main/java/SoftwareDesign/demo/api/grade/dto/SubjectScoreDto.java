package SoftwareDesign.demo.api.grade.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubjectScoreDto {
    private String subjectName;
    private Integer score;
    private String semester;
}