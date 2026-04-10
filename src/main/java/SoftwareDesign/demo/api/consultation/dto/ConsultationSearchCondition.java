package SoftwareDesign.demo.api.consultation.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ConsultationSearchCondition {
    private String studentName;   // 학생 이름으로 찾기
    private String teacherName;   // 상담 교사 이름으로 찾기
    private LocalDate startDate;  // 특정 기간 시작
    private LocalDate endDate;    // 특정 기간 끝
    private String keyword;       // 상담 내용 키워드
}