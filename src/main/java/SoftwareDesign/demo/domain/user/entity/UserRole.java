package SoftwareDesign.demo.domain.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "권한 상태 (ADMIN: 관리자, TEACHER: 선생님, STUDENT: 학생, PARENT: 학부모, GUEST: 게스트)")
public enum UserRole {
    ADMIN, TEACHER, STUDENT, PARENT, GUEST
}