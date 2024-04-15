package example.com.moneymergebe.domain.user.entity;

import example.com.moneymergebe.domain.user.entity.UserRole;
import example.com.moneymergebe.domain.user.entity.UserSocialEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tb_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String nickname;

    @Column(nullable=false)
    private String password;

    @Column(nullable=false)
    private String email;

    @Column(nullable=false)
    private String profileUrl;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private UserSocialEnum social;

    @Column
    private int character;

    @Column
    private int point;

//    @Column
//    private int alarm;

    @Column
    @CreationTimestamp
    private LocalDateTime created_at = LocalDateTime.now();

    @Column
    @UpdateTimestamp
    private LocalDateTime updated_at = LocalDateTime.now();


    @Builder
    public User(String nickname, String password, String email, String profileUrl, UserRole role, UserSocialEnum social, int character, int point){
        this.nickname=nickname;
        this.password=password;
        this.email=email;
        this.profileUrl=profileUrl;
        this.role=role;
        this.social=social;
        this.character=character;
        this.point=point;
    }
}
