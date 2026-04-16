package SoftwareDesign.demo.api.notification.dto;


import SoftwareDesign.demo.domain.grade.entity.Grade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GradeEvent {
    private Long studentId;
    private String letterGrade;
    private String semester;
    private String subjectName;
    private Integer score;

    public static GradeEvent from(Grade grade) {
        return GradeEvent.builder()
                .studentId(grade.getStudent().getId())
                .semester(grade.getSemester())
                .letterGrade(grade.getLetterGrade())
                .subjectName(grade.getSubject().getName())
                .score(grade.getScore())
                .build();
    }
}