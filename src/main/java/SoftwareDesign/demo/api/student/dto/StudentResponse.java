package SoftwareDesign.demo.api.student.dto;

import SoftwareDesign.demo.domain.student.entity.Student;
import lombok.Getter;

@Getter
public class StudentResponse {
    private final String name;
    private final String studentNumber;
    private final int grade;
    private final int classNum;
    private final int number;

    public StudentResponse(Student student) {
        this.name = student.getUser().getName(); // User 엔티티에서 이름을 가져옴
        this.studentNumber = student.getStudentNumber();
        this.grade = student.getGrade();
        this.classNum = student.getClassNum();
        this.number = student.getNumber();
    }
}