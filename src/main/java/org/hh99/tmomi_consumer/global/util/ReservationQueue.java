package org.hh99.tmomi_consumer.global.util;

import java.util.Set;

import org.hh99.reservation.dto.ReservationDto;
import org.hh99.tmomi.global.exception.GlobalException;
import org.hh99.tmomi.global.exception.message.ExceptionCode;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationQueue {

	private final RedisTemplate<String, ReservationDto> redisTemplate;
	final String key = "reservation";

	public void addQueue(ReservationDto reservationDto) {
		final long now = System.currentTimeMillis();
		redisTemplate.opsForZSet().add(key, reservationDto, now);
	}

	public Set<ReservationDto> getQueue(Long end) {
		Set<ReservationDto> queue = redisTemplate.opsForZSet().range(key, 0, end);
		if (queue == null) {
			throw new GlobalException(HttpStatus.NOT_FOUND, ExceptionCode.NOT_EXIST_QUEUE_IN_USER);
		}
		return queue;
	}

	public void deleteQueue(ReservationDto reservationDto) {
		redisTemplate.opsForZSet().remove(key, reservationDto);
	}

	public Long getRank(ReservationDto reservationDto) {
		return redisTemplate.opsForZSet().rank(key, reservationDto);
	}
}
