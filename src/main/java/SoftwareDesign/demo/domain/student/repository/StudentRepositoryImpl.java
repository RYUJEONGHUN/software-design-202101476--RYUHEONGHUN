package SoftwareDesign.demo.domain.student.repository;

import SoftwareDesign.demo.api.student.dto.QStudentSummaryResponse;
import SoftwareDesign.demo.api.student.dto.StudentSearchCondition;
import SoftwareDesign.demo.api.student.dto.StudentSummaryResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static SoftwareDesign.demo.domain.student.entity.QStudent.student;
import static SoftwareDesign.demo.domain.user.entity.QUser.user;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
public class StudentRepositoryImpl implements StudentRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<StudentSummaryResponse> search(StudentSearchCondition condition, Pageable pageable) {
        List<StudentSummaryResponse> content = queryFactory
                .select(new QStudentSummaryResponse(
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
}