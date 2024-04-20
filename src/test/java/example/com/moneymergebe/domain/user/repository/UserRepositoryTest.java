package example.com.moneymergebe.domain.user.repository;

import static org.junit.jupiter.api.Assertions.*;

import example.com.moneymergebe.domain.user.entity.User;
import example.com.moneymergebe.domain.user.entity.UserRole;
import example.com.moneymergebe.domain.user.entity.UserSocialEnum;
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
        user = User.builder().username("username").email("email").profileUrl("profileUrl").role(UserRole.USER).social(
            UserSocialEnum.KAKAO).characterId(1).points(200).alarm(true).attendance(true).build();
        userRepository.save(user);
    }

    @Test
    @DisplayName("findByUserId 테스트")
    void findByUserId() {
        // when
        User saveUser = userRepository.findByUserId(user.getUserId());

        // then
        assertEquals(user, saveUser);
    }

    @Test
    @DisplayName("findByUsername 테스트")
    void findByUsernameTest() {
        // when
        User saveUser = userRepository.findByUsername(user.getUsername());

        // then
        assertEquals(user, saveUser);
    }
}