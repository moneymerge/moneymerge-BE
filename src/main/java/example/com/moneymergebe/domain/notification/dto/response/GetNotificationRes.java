package example.com.moneymergebe.domain.notification.dto.response;

import example.com.moneymergebe.domain.notification.entity.Notification;
import example.com.moneymergebe.domain.notification.entity.NotificationType;
import jakarta.annotation.Nullable;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class GetNotificationRes {
    private Long notificationId;
    @Nullable
    private String content;
    private boolean isRead;
    private LocalDateTime createdAt;
    private NotificationType type;
    private String message;

    public GetNotificationRes(Notification notification) {
        this.notificationId = notification.getNotificationId();
        this.content = notification.getContent();
        this.isRead = notification.isRead();
        this.createdAt = notification.getCreatedAt();
        this.type = notification.getType();
        this.message = notification.getType().getMessage();
    }
}
