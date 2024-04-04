package example.com.meneymergebe.domain.user.repository;

import example.com.meneymergebe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
