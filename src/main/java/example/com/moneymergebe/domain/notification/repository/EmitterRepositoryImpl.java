package example.com.moneymergebe.domain.notification.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class EmitterRepositoryImpl implements EmitterRepository{

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    @Override
    public void deleteById(String emitterId) {
        emitters.remove(emitterId);
    }

    @Override
    public SseEmitter findById(String emitterId) {
        return emitters.get(emitterId);
    }

    @Override
    public Map<String, SseEmitter> findAllEmitterStartWithByUserId(Long userId) {
        return emitters.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith(Long.toString(userId)))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


}
