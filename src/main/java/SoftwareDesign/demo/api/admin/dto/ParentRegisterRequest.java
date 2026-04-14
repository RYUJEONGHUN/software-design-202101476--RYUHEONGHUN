package SoftwareDesign.demo.api.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ParentRegisterRequest {
    private List<Long> studentIds; // 연결할 자녀들의 ID 리스트
}
