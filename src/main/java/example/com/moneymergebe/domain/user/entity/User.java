package example.com.moneymergebe.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tb_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    private int characterId;

    @Column(nullable = false)
    private int points;

    @Column(nullable = false)
    private int alarm;

    @Column(nullable = false)
    private int attendance;

    @Column
    @CreationTimestamp
    private LocalDateTime created_at = LocalDateTime.now();

    @Column
    @UpdateTimestamp
    private LocalDateTime updated_at = LocalDateTime.now();

    @Builder
    private User(
        String username,
        String email,
        String profileUrl,
        UserRole role,
        UserSocialEnum social,
        int characterId,
        int points,
        int alarm,
        int attendance) {
        this.username = username;
        this.email = email;
        this.profileUrl = profileUrl;
        this.role = role;
        this.social = social;
        this.characterId=characterId;
        this.points=points;
        this.alarm=alarm;
        this.attendance=attendance;
    }

}
