package org.hh99.tmomi_consumer.global.util;

import lombok.RequiredArgsConstructor;
import org.hh99.reservation.dto.ReservationDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ReservationQueue {

    private final RedisTemplate<String, ReservationDto> redisTemplate;
    final String key = "reservation";

    // 대기열 진입
    public void addQueue(ReservationDto reservationDto) {
        final long now = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(key, reservationDto, now);
    }

    // 대기열 가져오기
    public Set<ReservationDto> getQueue(Long end) {
        Set<ReservationDto> queue =  redisTemplate.opsForZSet().range(key, 0, end);
        if(queue == null)  throw new IllegalArgumentException("입장 시킬 인원없음");

        return queue;
    }

    // 대기열에서 삭제
    public void deleteQueue(ReservationDto reservationDto) {
        redisTemplate.opsForZSet().remove(key, reservationDto);
    }

    // 대기 순서 반환
    public Long getRank(ReservationDto reservationDto) {
        return redisTemplate.opsForZSet().rank(key, reservationDto);
    }
}
