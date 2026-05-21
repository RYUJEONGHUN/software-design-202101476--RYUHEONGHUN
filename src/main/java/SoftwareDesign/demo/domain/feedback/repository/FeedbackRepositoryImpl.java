package SoftwareDesign.demo.domain.feedback.repository;

import SoftwareDesign.demo.api.feedback.dto.FeedbackSearchCondition;
import SoftwareDesign.demo.domain.feedback.entity.Feedback;
import SoftwareDesign.demo.domain.feedback.entity.FeedbackCategory;
import SoftwareDesign.demo.domain.user.entity.QUser;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import static SoftwareDesign.demo.domain.feedback.entity.QFeedback.feedback;
import static SoftwareDesign.demo.domain.student.entity.QStudent.student;
import static SoftwareDesign.demo.domain.teacher.entity.QTeacher.teacher;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
public class FeedbackRepositoryImpl implements FeedbackRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Feedback> searchForParent(Long studentId, FeedbackSearchCondition condition) {
        return search(studentId, condition, true);
    }

    @Override
    public List<Feedback> searchForStaff(Long studentId, FeedbackSearchCondition condition) {
        return search(studentId, condition, false);
    }

    private List<Feedback> search(Long studentId, FeedbackSearchCondition condition, boolean parentOnly) {
        QUser teacherUser = new QUser("teacherUser");
        QUser studentUser = new QUser("studentUser");

        return queryFactory
                .selectFrom(feedback)
                .join(feedback.teacher, teacher).fetchJoin()
                .join(teacher.user, teacherUser).fetchJoin()
                .join(feedback.student, student).fetchJoin()
                .join(student.user, studentUser).fetchJoin()
                .where(
                        feedback.student.id.eq(studentId),
                        visibleToParent(parentOnly),
                        categoryEq(condition.getCategory()),
                        keywordContains(condition.getKeyword()),
                        startDateGoe(condition.getStartDate()),
                        endDateLoe(condition.getEndDate())
                )
                .orderBy(feedback.createdAt.desc())
                .fetch();
    }
    private BooleanExpression visibleToParent(boolean parentOnly) {
        return parentOnly ? feedback.visibleToParent.isTrue() : null;
    }

    private BooleanExpression categoryEq(FeedbackCategory category) {
        return category != null ? feedback.category.eq(category) : null;
    }

    private BooleanExpression keywordContains(String keyword) {
        return hasText(keyword) ? feedback.content.contains(keyword) : null;
    }
    private BooleanExpression startDateGoe(LocalDate startDate) {
        return startDate != null ? feedback.createdAt.goe(startDate.atStartOfDay()) : null;
    }

    private BooleanExpression endDateLoe(LocalDate endDate) {
        return endDate != null ? feedback.createdAt.lt(endDate.plusDays(1).atStartOfDay()) : null;
    }
}
