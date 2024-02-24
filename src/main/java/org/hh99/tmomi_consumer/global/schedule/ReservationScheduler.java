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
    private static final long delay = 10000L;
    private final ReservationQueue reservationQueue;
    private final EmitterService emitterService;

    @Scheduled(fixedRate = delay)
    public void reservationScheduler() {
        // 입장처리할 사용자들
        Set<ReservationDto> queue = reservationQueue.enter();
        queue.parallelStream().forEach(emitterService::sendToSeatList);

        // 나머지 가져오기, 대기열 보여주기

    }
}
