package SoftwareDesign.demo.api.consultation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ConsultationDto {
    private Long id;
    private String teacherName;
    private String content;
    private LocalDate consultationDate;
}