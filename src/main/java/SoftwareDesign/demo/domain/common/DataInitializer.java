package SoftwareDesign.demo.domain.common;

import SoftwareDesign.demo.domain.subject.entity.Subject;
import SoftwareDesign.demo.domain.subject.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final SubjectRepository subjectRepository;

    @Override
    public void run(String... args) {
        if (subjectRepository.count() == 0) {
            subjectRepository.save(new Subject("국어"));
            subjectRepository.save(new Subject("영어"));
            subjectRepository.save(new Subject("수학"));
            subjectRepository.save(new Subject("알고리즘"));
            System.out.println("초기 과목 데이터가 생성");
        }
    }
}
