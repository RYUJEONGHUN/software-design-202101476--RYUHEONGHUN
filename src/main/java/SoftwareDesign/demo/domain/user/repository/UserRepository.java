package SoftwareDesign.demo.domain.user.repository;

import SoftwareDesign.demo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일(username)로 기존 가입 여부 확인
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}