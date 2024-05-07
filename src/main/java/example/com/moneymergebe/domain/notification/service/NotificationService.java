package example.com.moneymergebe.domain.notification.service;

import example.com.moneymergebe.domain.notification.dto.request.NotificationReq;
import example.com.moneymergebe.domain.notification.dto.response.NotificationRes;
import example.com.moneymergebe.domain.notification.entity.Notification;
import example.com.moneymergebe.domain.notification.repository.EmitterRepository;
import example.com.moneymergebe.domain.notification.repository.EmitterRepositoryImpl;
import example.com.moneymergebe.domain.notification.repository.NotificationRepository;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final EmitterRepository emitterRepository = new EmitterRepositoryImpl();
    private final NotificationRepository notificationRepository;
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 1시간
    private static final String SUBSCRIBE_MESSAGE = "EventStream Created.";
    private static final String SSE_EVENT_NAME = "sse";

    /**
     * SSE 연결
     * @param userId 사용자 ID
     * @param lastEventId 클라이언트가 마지막으로 수신한 데이터의 ID
     * @return 연결한 후 더미데이터, 유실된 데이터가 있으면 재전송
     */
    public SseEmitter subscribe(Long userId, String lastEventId) {
        String emitterId = userId + "_" + System.currentTimeMillis();
        log.info("emitterId = {}", emitterId);
        SseEmitter sseEmitter = createEmitter(emitterId);
        sendToClient(emitterId, SUBSCRIBE_MESSAGE);

        if(!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(userId));
            events.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendToClient(entry.getKey(), entry.getValue()));
        }

        return sseEmitter;
    }

    /**
     * 알림 생성
     * @param userId 알림을 받을 사용자
     * @param req 알림 종류
     */
    public void notify(Long userId, NotificationReq req) {
        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithByUserId(String.valueOf(userId));

        sseEmitters.forEach(
            (key, emitter) -> {
                Notification notification = notificationRepository.save(new Notification(req));
                NotificationRes res = new NotificationRes(notification);
                emitterRepository.saveEventCache(key, res);
                sendToClient(key, res);
            }
        );
    }

    private SseEmitter createEmitter(String emitterId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(emitterId, emitter);

        // Emitter가 완료될 때(모든 데이터가 성공적으로 전송된 상태) Emitter를 삭제한다.
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        // Emitter가 타임아웃 되었을 때(지정된 시간동안 어떠한 이벤트도 전송되지 않았을 때) Emitter를 삭제한다.
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        return emitter;
    }

    private void sendToClient(String emitterId, Object data) {
        SseEmitter sseEmitter = emitterRepository.findById(emitterId);
        if(sseEmitter != null) {
            try {
                sseEmitter.send(SseEmitter.event().id(emitterId).name(SSE_EVENT_NAME).data(data));
            } catch (IOException ex) {
                emitterRepository.deleteById(emitterId);
                sseEmitter.completeWithError(ex);
            }
        }
    }
}
