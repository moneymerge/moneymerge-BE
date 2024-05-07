package example.com.moneymergebe.domain.notification.service;

import example.com.moneymergebe.domain.notification.repository.EmitterRepository;
import example.com.moneymergebe.domain.notification.repository.EmitterRepositoryImpl;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final EmitterRepository emitterRepository = new EmitterRepositoryImpl();
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 1시간
    private static final String SUBSCRIBE_MESSAGE = "EventStream Created.";
    private static final String SSE_EVENT_NAME = "sse";

    public SseEmitter subscribe(Long userId) {
        String emitterId = userId + "_" + System.currentTimeMillis();
        log.info("emitterId = {}", emitterId);
        SseEmitter sseEmitter = createEmitter(emitterId);
        sendToClient(emitterId, SUBSCRIBE_MESSAGE);
        return sseEmitter;
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

    private void sendToClient(String emitterId, String data) {
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