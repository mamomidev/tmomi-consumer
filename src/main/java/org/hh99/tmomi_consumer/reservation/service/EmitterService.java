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
	public static final Long DEFAULT_TIMEOUT = -1L;

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

	public SseEmitter addEmitter(ReservationDto reservationDto) {
		String emitterId = reservationDto.getEmail() + "_" + reservationDto.getEventTimeId();
		SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

		emitter.onCompletion(() -> {
			emitterRepository.deleteById(emitterId);
			reservationQueue.deleteQueue(reservationDto);
		});

		emitter.onTimeout(emitter::complete);

		sendToClient(emitter, emitterId, "connected!"); // 503 에러방지 더미 데이터

		return emitter;
	}

	@Scheduled(fixedRate = 1000) // 3분마다 heartbeat 메세지 전달.
	public void sendQueueNum() {
		Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitters();
		sseEmitters.forEach((key, emitter) -> {
			ReservationDto reservationDto = new ReservationDto(key.split("_")[0], Long.parseLong(key.split("_")[1]));
			long rank = reservationQueue.getRank(reservationDto) + 1;
			if (rank > 50) {
				sendWaitNumberToClient(reservationDto, rank);
			}
		});
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