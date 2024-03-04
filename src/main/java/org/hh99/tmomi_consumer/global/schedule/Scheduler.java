package org.hh99.tmomi_consumer.global.schedule;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.hh99.reservation.dto.ReservationDto;
import org.hh99.tmomi_consumer.global.util.ReservationQueue;
import org.hh99.tmomi_consumer.emitter.Repository.EmitterRepository;
import org.hh99.tmomi_consumer.emitter.service.EmitterService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@RequiredArgsConstructor
public class Scheduler {
	private static final long delay = 1000L;
	private final ReservationQueue reservationQueue;
	private final EmitterService emitterService;
	private final EmitterRepository emitterRepository;
	@Scheduled(fixedRate = delay)
	public void sendSeatList() {
		// 입장처리할 사용자들
		long joinSize = 50L;
		Set<ReservationDto> enterQueue = reservationQueue.getQueue(joinSize - 1);
		enterQueue.parallelStream().forEach(reservationDto -> {
			emitterService.sendSeatListToClient(reservationDto);
			reservationQueue.deleteQueue(reservationDto);
		});
	}
	@Scheduled(fixedRate = 1000)
	public void sendQueueNum() {
		Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitters();
		sseEmitters.forEach((key, emitter) -> {
			ReservationDto reservationDto = new ReservationDto(key.split("_")[0], Long.parseLong(key.split("_")[1]));
			long rank = reservationQueue.getRank(reservationDto) + 1;
			if (rank > 50) {
				emitterService.sendToClient(emitter, key, rank);
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
