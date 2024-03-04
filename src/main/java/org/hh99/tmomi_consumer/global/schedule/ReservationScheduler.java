package org.hh99.tmomi_consumer.global.schedule;

import java.util.Set;

import org.hh99.reservation.dto.ReservationDto;
import org.hh99.tmomi_consumer.global.util.ReservationQueue;
import org.hh99.tmomi_consumer.reservation.service.EmitterService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationScheduler {
	private static final long delay = 1000L;
	private final ReservationQueue reservationQueue;
	private final EmitterService emitterService;

	@Scheduled(fixedRate = delay)
	public void reservationScheduler() {
		// 입장처리할 사용자들
		long joinSize = 50L;
		Set<ReservationDto> enterQueue = reservationQueue.getQueue(joinSize - 1);
		enterQueue.parallelStream().forEach(reservationDto -> {
			emitterService.sendSeatListToClient(reservationDto);
			reservationQueue.deleteQueue(reservationDto);
		});
	}
}
