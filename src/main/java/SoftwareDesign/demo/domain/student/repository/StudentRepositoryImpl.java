package SoftwareDesign.demo.domain.student.repository;

import SoftwareDesign.demo.api.consultation.dto.ConsultationDto;
import SoftwareDesign.demo.api.feedback.dto.FeedbackDto;
import SoftwareDesign.demo.api.grade.dto.SubjectScoreDto;
import SoftwareDesign.demo.api.student.dto.QStudentSummaryResponse;
import SoftwareDesign.demo.api.student.dto.StudentSearchCondition;
import SoftwareDesign.demo.api.student.dto.StudentSummaryResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.List;
import static SoftwareDesign.demo.domain.consultation.entity.QConsultation.consultation;
import static SoftwareDesign.demo.domain.feedback.entity.QFeedback.feedback;
import static SoftwareDesign.demo.domain.grade.entity.QGrade.grade;
import static SoftwareDesign.demo.domain.student.entity.QStudent.student;
import static SoftwareDesign.demo.domain.subject.entity.QSubject.subject;
import static SoftwareDesign.demo.domain.teacher.entity.QTeacher.teacher;
import static SoftwareDesign.demo.domain.user.entity.QUser.user;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
public class StudentRepositoryImpl implements StudentRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<StudentSummaryResponse> search(StudentSearchCondition condition, Pageable pageable) {
        List<StudentSummaryResponse> content = queryFactory
                .select(new QStudentSummaryResponse(
                        student.id,
                        user.name,
                        student.studentNumber,
                        student.grade,
                        student.classNum
                ))
                .from(student)
                .join(student.user, user)
                .where(
                        nameContains(condition.getName()),
                        gradeEq(condition.getGrade()),
                        classNumEq(condition.getClassNum())
                )
                .offset(pageable.getOffset()) //  몇 번째부터?
                .limit(pageable.getPageSize()) // 몇 개?
                .fetch();

        Long total = queryFactory
                .select(student.count())
                .from(student)
                .where(
                        nameContains(condition.getName()),
                        gradeEq(condition.getGrade()),
                        classNumEq(condition.getClassNum()) //
                )
                .fetchOne();

        // 2. 만약 null이면 0L로 처리
        long totalCount = (total != null) ? total : 0L;

        return new PageImpl<>(content, pageable, totalCount);
    }
    // 1. 학년 필터 (Integer로 받아야 null 체크가 가능)
    private BooleanExpression gradeEq(Integer grade) {
        return grade != null ? student.grade.eq(grade) : null;
    }

    // 2. 반 필터
    private BooleanExpression classNumEq(Integer classNum) {
        return classNum != null ? student.classNum.eq(classNum) : null;
    }

    // 3. 이름 검색 (contains를 쓰면 '정훈'만 쳐도 '류정훈'이 검색된다)
    private BooleanExpression nameContains(String studentName) {
        return hasText(studentName) ? user.name.contains(studentName) : null;
    }


    @Override
    public List<SubjectScoreDto> findRecentScoresByStudentId(Long studentId) {
        return queryFactory
                .select(Projections.constructor(SubjectScoreDto.class,
                        subject.name, // DTO의 첫 번째 인자: 과목명
                        grade.score, // DTO의 두 번째 인자: 점수
                        grade.semester
                ))
                .from(grade)
                .join(grade.subject, subject)
                .where(grade.student.id.eq(studentId))
                .orderBy(grade.semester.desc())
                .fetch();
    }

    @Override
    public List<FeedbackDto> findRecentFeedbacks(Long studentId) {
        return queryFactory
                .select(Projections.constructor(FeedbackDto.class,
                        feedback.id,
                        user.name, // 선생님 성함 (teacher -> user 조인)
                        feedback.content,
                        feedback.createdAt
                ))
                .from(feedback)
                .join(feedback.teacher, teacher) // 선생님 조인
                .join(teacher.user, user)         // 유저 정보 조인
                .where(feedback.student.id.eq(studentId))
                .orderBy(feedback.createdAt.desc()) // 최근순
                .limit(3) // 딱 3개만!
                .fetch();
    }

    // 2. 상담 3건 조회
    @Override
    public List<ConsultationDto> findRecentConsultations(Long studentId) {
        return queryFactory
                .select(Projections.constructor(ConsultationDto.class,
                        consultation.id,
                        user.name, // 선생님 성함
                        consultation.content,
                        consultation.consultationDate
                ))
                .from(consultation)
                .join(consultation.teacher, teacher)
                .join(teacher.user, user)
                .where(consultation.student.id.eq(studentId))
                .orderBy(consultation.consultationDate.desc()) // 최근 상담일순
                .limit(3)
                .fetch();
    }
}