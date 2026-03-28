package SoftwareDesign.demo.api.teacher;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/teacher")
public class TeacherController {

    @GetMapping("/test")
    public String teacherTest() {
        return "선생님 전용 메뉴 접속 성공! 허허허, 환영합니다.";
    }
}