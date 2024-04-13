package example.com.moneymergebe.domain.user.entity;

import example.com.moneymergebe.domain.book.entity.BookUser;
import example.com.moneymergebe.domain.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tb_user")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String username; // 닉네임

    private String email;

    private String profileUrl; // 프로필 사진 URL

    // TODO: 소셜 종류 추가

    private Long characterId; // 현재 캐릭터

    private Long point;

    private boolean alarm; // 알람 설정

    @Enumerated(value = EnumType.STRING)
    private UserRole role; // 권한

    @OneToMany(mappedBy = "user")
    private List<BookUser> bookUserList = new ArrayList<>();

    public void updateUsername(String username) {
        this.username = username;
    }

    public void changeAlarm() {
        this.alarm = !alarm;
    }

    public void updateProfile(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
