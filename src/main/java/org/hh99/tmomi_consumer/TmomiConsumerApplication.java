package org.hh99.tmomi_consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
public class TmomiConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TmomiConsumerApplication.class, args);
	}

}
