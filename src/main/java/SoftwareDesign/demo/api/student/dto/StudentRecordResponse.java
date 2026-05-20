package SoftwareDesign.demo.api.student.dto;

import SoftwareDesign.demo.domain.studentrecord.entity.StudentRecord;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudentRecordResponse {
    private Long id;
    private Long studentId;
    private int schoolYear;
    private String semester;
    private String specialNote;
    private String behaviorNote;
    private String careerHope;
    private String healthNote;

    public static StudentRecordResponse from(StudentRecord record) {
        return StudentRecordResponse.builder()
                .id(record.getId())
                .studentId(record.getStudent().getId())
                .schoolYear(record.getSchoolYear())
                .semester(record.getSemester())
                .specialNote(record.getSpecialNote())
                .behaviorNote(record.getBehaviorNote())
                .careerHope(record.getCareerHope())
                .healthNote(record.getHealthNote())
                .build();
    }
}