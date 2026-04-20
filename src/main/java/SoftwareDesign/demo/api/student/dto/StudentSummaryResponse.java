package SoftwareDesign.demo.api.student.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class StudentSummaryResponse {
    // 1. 기본 정보
    private Long id;
    private String name;
    private String studentNumber;
    private Integer grade;
    private Integer classNum;

    // 2. 출결 요약 (예: 출석 20회, 결석 2회 등)
    private long attendanceCount;
    private long absenceCount;

    // 3. 최근 상담 내역
    private String lastConsultationContent;

    // 4. 성적 요약 (평균 점수)
    private Double averageScore;

    @QueryProjection // Q클래스 생성 시 이 생성자도 포함되게 하네!
    public StudentSummaryResponse(Long id, String name, String studentNumber, Integer grade, Integer classNum) {
        this.id = id;
        this.name = name;
        this.studentNumber = studentNumber;
        this.grade = grade;
        this.classNum = classNum;
    }
}
