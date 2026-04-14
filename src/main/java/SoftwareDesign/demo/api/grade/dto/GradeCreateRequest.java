package SoftwareDesign.demo.api.grade.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GradeCreateRequest {
    private Long studentId;
    private int score;
    private String semester;
}

