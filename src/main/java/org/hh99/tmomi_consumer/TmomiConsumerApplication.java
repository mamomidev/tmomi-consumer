package org.hh99.tmomi_consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
@EntityScan(basePackages = {"org.hh99.tmomi"})
@EnableRedisRepositories(basePackages = {"org.hh99.tmomi.global.redis"})
@EnableJpaRepositories(basePackages = {"org.hh99.tmomi"})
@EnableElasticsearchRepositories(basePackages = {"org.hh99.tmomi.domain.reservation.respository"})
public class TmomiConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TmomiConsumerApplication.class, args);
	}

}
