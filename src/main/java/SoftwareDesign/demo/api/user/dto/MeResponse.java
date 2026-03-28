package SoftwareDesign.demo.api.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MeResponse {
    private String email;
    private String name;
    private String role;

    // 학생일 경우에만 채워질 필드
    private StudentDetail student;

    // 교사일 경우에만 채워질 필드
    private TeacherDetail teacher;

    @Getter
    @Builder
    public static class StudentDetail {
        private String studentNumber;
        private int grade;
        private int classNum;
        private int number;
    }

    @Getter
    @Builder
    public static class TeacherDetail {
        private String subject;
    }
}