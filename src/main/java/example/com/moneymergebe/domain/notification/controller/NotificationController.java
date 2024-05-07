package example.com.moneymergebe.domain.notification.controller;

import example.com.moneymergebe.domain.notification.service.NotificationService;
import example.com.moneymergebe.global.response.CommonResponse;
import example.com.moneymergebe.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    /**
     * SSE 연결
     */
    @GetMapping(value = "/subscription", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestHeader(value = "Last-Event_ID", required = false, defaultValue = "") String lastEventId) {
        return notificationService.subscribe(userDetails.getUser().getUserId(), lastEventId);
    }

    /**
     * 알림 테스트
     */
    @PostMapping("/{userId}")
    public void notify(@PathVariable Long userId) {
        notificationService.notify(userId, "data");
    }
}
