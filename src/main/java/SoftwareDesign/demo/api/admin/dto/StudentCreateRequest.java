package SoftwareDesign.demo.api.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudentCreateRequest {
    private String studentNumber; // 학번
    private int grade;            // 학년
    private int classNum;         // 반
    private int number;           // 번호
}