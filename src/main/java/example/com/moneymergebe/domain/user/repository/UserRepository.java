package example.com.moneymergebe.domain.user.repository;

import example.com.moneymergebe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserId(Long userId);
    User findByUsername(String username);
    User findByEmail(String email);
}
