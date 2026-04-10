package SoftwareDesign.demo.domain.student.repository;

import SoftwareDesign.demo.api.student.dto.StudentSearchCondition;
import SoftwareDesign.demo.api.student.dto.StudentSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentRepositoryCustom{
    Page<StudentSummaryResponse> search(StudentSearchCondition condition, Pageable pageable);
}
