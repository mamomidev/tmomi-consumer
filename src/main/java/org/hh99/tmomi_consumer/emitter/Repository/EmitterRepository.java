package org.hh99.tmomi_consumer.emitter.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class EmitterRepository {
	private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

	public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
		emitters.put(emitterId, sseEmitter);
		return sseEmitter;
	}

	public Map<String, SseEmitter> findAllEmitters() {
		return new HashMap<>(emitters);
	}

	public Map<String, SseEmitter> findAllEmitterStartWithById(String memberId) {
		return emitters.entrySet().stream()
			.filter(entry -> entry.getKey().startsWith(memberId))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	public void deleteById(String emitterId) {
		emitters.remove(emitterId);
	}
}