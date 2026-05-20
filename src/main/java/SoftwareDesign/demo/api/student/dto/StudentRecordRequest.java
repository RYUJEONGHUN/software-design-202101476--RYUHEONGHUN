package SoftwareDesign.demo.api.student.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudentRecordRequest {
    private int schoolYear;
    private String semester;
    private String specialNote;
    private String behaviorNote;
    private String careerHope;
    private String healthNote;
}