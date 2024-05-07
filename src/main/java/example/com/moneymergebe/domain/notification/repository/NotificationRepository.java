package example.com.moneymergebe.domain.notification.repository;

import example.com.moneymergebe.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
