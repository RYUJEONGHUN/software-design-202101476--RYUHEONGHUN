package SoftwareDesign.demo.domain.consultation.repository;


import SoftwareDesign.demo.api.consultation.dto.ConsultationSearchCondition;
import SoftwareDesign.demo.domain.consultation.entity.Consultation;
import SoftwareDesign.demo.domain.user.entity.QUser;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import static SoftwareDesign.demo.domain.consultation.entity.QConsultation.consultation;
import static SoftwareDesign.demo.domain.student.entity.QStudent.student;
import static SoftwareDesign.demo.domain.teacher.entity.QTeacher.teacher;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
public class ConsultationRepositoryImpl implements ConsultationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Consultation> search(ConsultationSearchCondition condition) {

        QUser studentUser = new QUser("studentUser");
        QUser teacherUser = new QUser("teacherUser");

        // Q클래스 호출 (QConsultation.consultation)
        return queryFactory
                .selectFrom(consultation)
                .leftJoin(consultation.student, student).fetchJoin() // 페치 조인으로 성능 최적화
                .leftJoin(student.user, studentUser).fetchJoin()
                .leftJoin(consultation.teacher, teacher).fetchJoin()
                .leftJoin(teacher.user, teacherUser).fetchJoin()
                .where(
                        studentIdEq(condition.getStudentId()),
                        studentNameEq(condition.getStudentName()),
                        teacherNameEq(condition.getTeacherName()),
                        betweenDate(condition.getStartDate(), condition.getEndDate())
                )
                .orderBy(consultation.consultationDate.desc())
                .fetch();
    }

    // --- 아래는 동적 쿼리를 위한 BooleanExpression 메서드들이라네 ---

    private BooleanExpression studentIdEq(Long studentId) {
        return studentId != null ? student.id.eq(studentId) : null;
    }

    private BooleanExpression studentNameEq(String studentName) {
        return hasText(studentName) ? student.user.name.eq(studentName) : null;
    }

    // 1. 선생님 이름 필터
    private BooleanExpression teacherNameEq(String teacherName) {
        return hasText(teacherName) ? teacher.user.name.eq(teacherName) : null;
    }

    // 2. 날짜 기간 필터
    private BooleanExpression betweenDate(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return null; // 기간 설정 안 하면 전체 조회!
        }
        return consultation.consultationDate.between(startDate, endDate);
    }
}
