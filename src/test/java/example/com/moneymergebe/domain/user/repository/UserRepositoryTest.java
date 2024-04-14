package example.com.moneymergebe.domain.user.repository;

import static org.junit.jupiter.api.Assertions.*;

import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.domain.user.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    static User user;

    @BeforeEach
    void setUser() {
        user = new User("username", "email", "profileUrl", 1L, 200L, true, UserRole.USER);
        userRepository.save(user);
    }

    @Test
    @DisplayName("findById 테스트")
    void findByIdTest() {
        // when
        User saveUser = userRepository.findById(user.getUserId()).orElse(null);

        // then
        assertEquals(user, saveUser);
    }

    @Test
    @DisplayName("findByUsername 테스트")
    void findByUsernameTest() {
        // when
        User saveUser = userRepository.findByUsername(user.getUsername()).orElse(null);

        // then
        assertEquals(user, saveUser);
    }
}