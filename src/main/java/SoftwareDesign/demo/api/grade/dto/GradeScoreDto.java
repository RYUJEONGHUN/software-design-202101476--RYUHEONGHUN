package SoftwareDesign.demo.api.grade.dto;


import SoftwareDesign.demo.domain.grade.entity.Grade;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GradeScoreDto {
    private Long gradeId;
    private String subjectName;
    private Integer score;
    private String letterGrade;
    private String semester;
    private Double classAverage;
    private Double totalAverage;

    public static GradeScoreDto from(Grade grade, Double classAverage, Double totalAverage) {
        return new GradeScoreDto(
                grade.getId(),
                grade.getSubject().getName(),
                grade.getScore(),
                grade.getLetterGrade(),
                grade.getSemester(),
                classAverage,
                totalAverage
        );
    }
}