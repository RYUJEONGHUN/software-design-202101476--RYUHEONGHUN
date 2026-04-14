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
            subjectRepository.save(new Subject("국어",1));
            subjectRepository.save(new Subject("영어",1));
            subjectRepository.save(new Subject("수학",1));
            subjectRepository.save(new Subject("알고리즘",1));
            subjectRepository.save(new Subject("데이터프로그래밍",2));
            subjectRepository.save(new Subject("자료구조",2));
            subjectRepository.save(new Subject("컴퓨터구조",2));
            subjectRepository.save(new Subject("대학수학",2));
            subjectRepository.save(new Subject("네트워크보안",3));
            subjectRepository.save(new Subject("웹프로그래밍",3));
            subjectRepository.save(new Subject("운영체제",3));
            subjectRepository.save(new Subject("리눅스",3));
            System.out.println("초기 과목 데이터가 생성");
        }
    }
}
