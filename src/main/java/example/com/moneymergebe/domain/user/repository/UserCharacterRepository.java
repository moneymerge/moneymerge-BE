package example.com.moneymergebe.domain.user.repository;

import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.domain.user.entity.UserCharacter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCharacterRepository extends JpaRepository<UserCharacter, Long> {
    Page<UserCharacter> findAllByUser(User user, Pageable pageable);
}
