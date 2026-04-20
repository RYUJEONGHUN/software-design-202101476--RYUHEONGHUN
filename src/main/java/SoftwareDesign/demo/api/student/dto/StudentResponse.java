package SoftwareDesign.demo.api.student.dto;

import SoftwareDesign.demo.domain.student.entity.Student;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudentResponse {
    private String name;
    private String studentNumber;
    private int grade;
    private int classNum;
    private int number;

    public StudentResponse(Student student) {
        this.name = student.getUser().getName();
        this.studentNumber = student.getStudentNumber();
        this.grade = student.getGrade();
        this.classNum = student.getClassNum();
        this.number = student.getNumber();
    }
}
