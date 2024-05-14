package example.com.moneymergebe.domain.notification.repository;

import example.com.moneymergebe.domain.notification.entity.Notification;
import example.com.moneymergebe.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByUser(User user);

    Notification findByNotificationId(Long notificationId);

    int countAllByUserAndIsReadIsFalse(User user);
}
