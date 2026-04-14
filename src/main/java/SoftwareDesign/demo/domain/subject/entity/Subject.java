package SoftwareDesign.demo.domain.subject.entity;

import SoftwareDesign.demo.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="subjects")
public class Subject extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private int grade; //  1, 2, 3학년 구분 필드 추가!

    @Builder
    public Subject(String name, int grade) {
        this.name = name;
        this.grade = grade;
    }


}
