package SoftwareDesign.demo.api.teacher.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeacherDashboardResponse {
    private String name;
    private String teacherIdNum;
    private String subjectName;
    private String role;
    private long scheduledConsultationCount; // 오늘 이후의 상담 개수
    //private long unreadNotificationCount;    // 읽지 않은 알림 개수
}