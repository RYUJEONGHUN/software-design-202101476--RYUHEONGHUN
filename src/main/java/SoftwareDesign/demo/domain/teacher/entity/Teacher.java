package SoftwareDesign.demo.domain.teacher.entity;

import SoftwareDesign.demo.domain.common.BaseTimeEntity;
import SoftwareDesign.demo.domain.subject.entity.Subject;
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
    @MapsId  //User의 PK를 Teacher의 PK로 매핑
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) //한 과목을 여러 선생님이 가르칠 수 있으니
    @JoinColumn(name = "subject_id")
    private Subject subject;      // 담당 과목

    private String teacherIdNum; // 교직원 번호

    @Builder
    public Teacher(User user,Subject subject, String teacherIdNum) {
        this.user = user;
        this.subject = subject;
        this.teacherIdNum = teacherIdNum;
    }
}