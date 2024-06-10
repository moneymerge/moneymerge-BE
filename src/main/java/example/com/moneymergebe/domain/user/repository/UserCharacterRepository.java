package example.com.moneymergebe.domain.user.repository;

import example.com.moneymergebe.domain.character.entity.Character;
import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.domain.user.entity.UserCharacter;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCharacterRepository extends JpaRepository<UserCharacter, Long> {
    Page<UserCharacter> findAllByUser(User user, Pageable pageable);

    List<UserCharacter> findAllByUser(User user);

    UserCharacter findByUserAndCharacter(User user, Character character);
}
