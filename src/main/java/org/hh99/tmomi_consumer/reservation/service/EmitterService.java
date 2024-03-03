package org.hh99.tmomi_consumer.reservation.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.hh99.reservation.dto.ReservationDto;
import org.hh99.tmomi.domain.reservation.Status;
import org.hh99.tmomi.domain.reservation.document.ElasticSearchReservation;
import org.hh99.tmomi.domain.reservation.respository.ElasticSearchReservationRepository;
import org.hh99.tmomi_consumer.global.util.ReservationQueue;
import org.hh99.tmomi_consumer.reservation.Repository.EmitterRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmitterService {
	private final EmitterRepository emitterRepository;
	private final ElasticSearchReservationRepository elasticSearchReservationRepository;
	private final ReservationQueue reservationQueue;
	public static final Long DEFAULT_TIMEOUT = 1000L;

	@KafkaListener(topics = "reservation", groupId = "group_1")
	public void listen(ReservationDto reservationDto) {
		reservationQueue.addQueue(reservationDto);
	}

	public void sendSeatListToClient(ReservationDto reservationDto) {
		Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithById(reservationDto.getEmail());
		sseEmitters.forEach(
			(key, emitter) -> {
				List<ElasticSearchReservation> elasticSeatList = elasticSearchReservationRepository.findAllByEventTimesIdAndStatus(
					reservationDto.getEventTimeId(), Status.NONE);

				emitterRepository.saveEventCache(key, reservationDto);
				sendToClient(emitter, key, elasticSeatList);
				emitterRepository.deleteById(reservationDto.getEmail());
				emitter.complete();
			}
		);
	}

	public void sendWaitNumberToClient(ReservationDto reservationDto, Long rank) {
		Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithById(reservationDto.getEmail());
		sseEmitters.forEach(
			(key, emitter) -> {
				emitterRepository.saveEventCache(key, reservationDto);
				sendToClient(emitter, key, rank);
			}
		);
	}

	private void sendToClient(SseEmitter emitter, String emitterId, Object data) {
		try {
			emitter.send(SseEmitter.event()
				.id(emitterId)
				.data(data));
		} catch (IOException e) {
			emitterRepository.deleteById(emitterId);
		}
	}

	public SseEmitter addEmitter(String userId, String lastEventId) {
		String emitterId = userId + "_" + System.currentTimeMillis();
		SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

		emitter.onCompletion(() -> {
			emitterRepository.deleteById(emitterId);
		});

		emitter.onTimeout(() -> {
			emitterRepository.deleteById(emitterId);
		});

		sendToClient(emitter, emitterId, "connected!"); // 503 에러방지 더미 데이터

		if (!lastEventId.isEmpty()) {
			Map<String, Object> events = emitterRepository.findAllEventCacheStartWithById(userId);
			events.entrySet().stream()
				.filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
				.forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
		}
		return emitter;
	}

	@Scheduled(fixedRate = 180000) // 3분마다 heartbeat 메세지 전달.
	public void sendHeartbeat() {
		Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitters();
		sseEmitters.forEach((key, emitter) -> {
			try {
				emitter.send(SseEmitter.event().id(key).name("heartbeat").data(""));
			} catch (IOException e) {
				emitterRepository.deleteById(key);
			}
		});
	}
}