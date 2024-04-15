package example.com.moneymergebe.domain.point.entity;

import example.com.moneymergebe.domain.common.BaseEntity;
import example.com.moneymergebe.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tb_point")
public class Point extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointId;

    private String detail;
    private Long point;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Point(String detail, Long point, User user) {
        this.detail = detail;
        this.point = point;
        this.user = user;
    }
}
