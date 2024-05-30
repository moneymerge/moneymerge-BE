package example.com.moneymergebe.domain.notification.dto.response;

import example.com.moneymergebe.domain.notification.entity.Notification;
import example.com.moneymergebe.domain.notification.entity.NotificationType;
import jakarta.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Getter;

@Getter
public class GetNotificationRes {
    private Long notificationId;
    @Nullable
    private String content;
    private boolean isRead;
    private String createdAt;
    private String type;
    private String message;

    public GetNotificationRes(Notification notification) {
        this.notificationId = notification.getNotificationId();
        this.content = notification.getContent();
        this.isRead = notification.isRead();
        this.createdAt = notification.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.type = notification.getType().getMessage();
        this.message = notification.getType().getMessage();
    }
}
