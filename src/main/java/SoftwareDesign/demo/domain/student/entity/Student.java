package SoftwareDesign.demo.domain.student.entity;

import SoftwareDesign.demo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "students")
public class Student {

    @Id
    private Long id; // User의 ID를 그대로 사용 (1:1 관계)

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // User의 PK를 Student의 PK로 매핑
    @JoinColumn(name = "user_id")
    private User user;

    private String studentNumber; // 학번 (예: 20240001)
    private int grade;            // 학년
    private int classNum;         // 반
    private int number;           // 번호

    @Builder
    public Student(User user, String studentNumber, int grade, int classNum, int number) {
        this.user = user;
        this.studentNumber = studentNumber;
        this.grade = grade;
        this.classNum = classNum;
        this.number = number;
    }
}