package example.com.moneymergebe.domain.notification.repository;

import java.util.Map;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EmitterRepository {
    SseEmitter save(String emitterId, SseEmitter sseEmitter);

    void deleteById(String emitterId);

    SseEmitter findById(String emitterId);

    Map<String, SseEmitter> findAllEmitterStartWithByUserId(Long userId);
}
