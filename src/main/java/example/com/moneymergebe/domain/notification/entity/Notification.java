package example.com.moneymergebe.domain.notification.entity;

import example.com.moneymergebe.domain.common.BaseEntity;
import example.com.moneymergebe.domain.notification.dto.request.NotificationReq;
import example.com.moneymergebe.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tb_notification")
public class Notification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    private String detail;

    @Column(nullable = false)
    private boolean isRead;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    public Notification(NotificationReq req, boolean isRead, User user) {
        this.detail = req.getDetail();
        this.isRead = isRead;
        this.type = req.getType();
        this.user = user;
    }

    public Notification(NotificationType type, String detail, User user) {
        this.detail = detail;
        this.isRead = false;
        this.type = type;
        this.user = user;
    }

    public void read() {
        this.isRead = true;
    }
}
