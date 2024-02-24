package org.hh99.tmomi_consumer.global.util;

import lombok.RequiredArgsConstructor;
import org.hh99.reservation.dto.ReservationDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ReservationQueue {

    private final RedisTemplate<String, Object> redisTemplate;
    final String key = "reservation";

    // 대기열 진입
    public void addQueue(ReservationDto reservationDto) {
        final long now = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(key, reservationDto, now);
    }

    // 내 대기 순서 반환 or 입장 처리
    public Long getRank() {
        Long size = redisTemplate.opsForZSet().zCard(key); // 대기 인원
        if (size == null) {
            // 바로 입장?
            return null;
        }
        Set<Object> queue =  redisTemplate.opsForZSet().range(key, 0, size);

        if(queue == null) {
            // 바로 입장...?
            return null;
        }
        queue.parallelStream().forEach(System.out::println

        );

        return null;
    }

    // 대기 순서 50위 미만이면 좌석 반환, 아니면 내 대기 순서 반환

}
