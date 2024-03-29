package org.hh99.tmomi_consumer.global.config;

import org.hh99.reservation.dto.ReservationDto;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class RedisConfig {

	private final RedisProperties redisProperties;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
	}

	@Bean
	public RedisTemplate<String, ReservationDto> redisTemplate() {
		RedisTemplate<String, ReservationDto> redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ReservationDto.class));
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		return redisTemplate;
	}
}
