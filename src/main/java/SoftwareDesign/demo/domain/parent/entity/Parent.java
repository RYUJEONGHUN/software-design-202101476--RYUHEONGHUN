package SoftwareDesign.demo.domain.parent.entity;


import SoftwareDesign.demo.domain.common.BaseTimeEntity;
import SoftwareDesign.demo.domain.student.entity.Student;
import SoftwareDesign.demo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "parents")
public class Parent extends BaseTimeEntity {

    @Id
    private Long id; // User의 ID를 PK로 사용 (1:1 관계)

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // User의 PK를 Parent의 PK로 매핑
    @JoinColumn(name = "user_id")
    private User user;

    // 부모님 한 명에 연결된 여러 자녀 매핑
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParentChild> children = new ArrayList<>();

    @Builder
    public Parent(User user) {
        this.user = user;
    }

    // 자녀 추가 편의 메서드
    public void addChild(Student student) {
        ParentChild pc = ParentChild.builder()
                .parent(this)
                .student(student)
                .build();
        this.children.add(pc);
    }
}
