package example.com.moneymergebe.domain.notification.dto.request;

import example.com.moneymergebe.domain.notification.entity.NotificationType;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationReq {
    private NotificationType type;
    @Nullable
    private String content;
}
