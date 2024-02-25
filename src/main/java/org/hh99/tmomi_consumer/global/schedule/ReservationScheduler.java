package org.hh99.tmomi_consumer.global.schedule;

import lombok.RequiredArgsConstructor;

import org.hh99.reservation.dto.ReservationDto;
import org.hh99.tmomi_consumer.global.util.ReservationQueue;
import org.hh99.tmomi_consumer.reservation.service.EmitterService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ReservationScheduler {
	private static final long delay = 20000L;
	private final ReservationQueue reservationQueue;
	private final EmitterService emitterService;

	@Scheduled(fixedRate = delay)
	public void reservationScheduler() {
		// 입장처리할 사용자들
		Set<ReservationDto> enterQueue = reservationQueue.getQueue(4L);
		enterQueue.parallelStream().forEach(reservationDto -> {
			emitterService.sendSeatListToClient(reservationDto);
			reservationQueue.deleteQueue(reservationDto);
		});

		if (enterQueue.size() == 5) {
			// 나머지 대기열 가져오기, 사용자에게 대기 순위 보내주기
			Set<ReservationDto> waitQueue = reservationQueue.getQueue(-1L);
			waitQueue.parallelStream().forEach(reservationDto -> {
				Long rank = reservationQueue.getRank(reservationDto) + 1;
				emitterService.sendWaitNumberToClient(reservationDto, rank);
			});
		}
	}
}
