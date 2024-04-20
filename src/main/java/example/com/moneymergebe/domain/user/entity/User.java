package example.com.moneymergebe.domain.user.entity;

import example.com.moneymergebe.domain.book.entity.BookUser;
import jakarta.persistence.Column;
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
import lombok.Builder;
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

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String profileUrl;

    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserSocialEnum social;

    @Column(nullable = false)
    private long characterId;

    @Column(nullable = false)
    private int points;

    @Column(nullable = false)
    private boolean alarm;

    @Column(nullable = false)
    private boolean attendance;

    @OneToMany(mappedBy = "user")
    private List<BookUser> bookUserList = new ArrayList<>();

    @Builder
    private User(
        String username,
        String email,
        String profileUrl,
        UserRole role,
        UserSocialEnum social,
        long characterId,
        int points,
        boolean alarm,
        boolean attendance) {
        this.username = username;
        this.email = email;
        this.profileUrl = profileUrl;
        this.role = role;
        this.social = social;
        this.characterId = characterId;
        this.points = points;
        this.alarm = alarm;
        this.attendance = attendance;
    }

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
