package SoftwareDesign.demo.api.student.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentSearchCondition {
    private Integer grade;    // 학년
    private Integer classNum; // 반
    private String name;      // 학생 이름
}