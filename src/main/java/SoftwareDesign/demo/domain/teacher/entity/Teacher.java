package SoftwareDesign.demo.domain.teacher.entity;

import SoftwareDesign.demo.domain.common.BaseTimeEntity;
import SoftwareDesign.demo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "teachers")
public class Teacher extends BaseTimeEntity {

    @Id
    private Long id; // User의 PK를 빌려 씀

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private String subject;      // 담당 과목
    private String teacherIdNum; // 교직원 번호 (나중에 쓸 수도 있으니!)

    @Builder
    public Teacher(User user, String subject, String teacherIdNum) {
        this.user = user;
        this.subject = subject;
        this.teacherIdNum = teacherIdNum;
    }
}