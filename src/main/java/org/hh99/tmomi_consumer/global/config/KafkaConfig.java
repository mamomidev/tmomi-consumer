package org.hh99.tmomi_consumer.global.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.hh99.reservation.dto.ReservationDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class KafkaConfig {

	@Value(value = "${spring.kafka.bootstrap-servers}")
	private String bootstrapAddress;
	@Value(value = "${aws.access-key}")
	private String awsAccessKeyId;
	@Value(value = "${aws.secret-access-key}")
	private String awsSecretAccessKey;

	@Bean
	public ConsumerFactory<String, ReservationDto> consumerFactory() {
		Map<String, Object> config = new HashMap<>();
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		config.put(ConsumerConfig.GROUP_ID_CONFIG, "group_1");
		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

		config.put("AWS_ACCESS_KEY_ID", awsAccessKeyId);
		config.put("AWS_SECRET_ACCESS_KEY", awsSecretAccessKey);

		config.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
		config.put(SaslConfigs.SASL_MECHANISM, "AWS_MSK_IAM");
		config.put(SaslConfigs.SASL_JAAS_CONFIG,"software.amazon.msk.auth.iam.IAMLoginModule required;");
		config.put(SaslConfigs.SASL_CLIENT_CALLBACK_HANDLER_CLASS, "software.amazon.msk.auth.iam.IAMClientCallbackHandler");

		JsonDeserializer<ReservationDto> deserializer = new JsonDeserializer<>(ReservationDto.class);
		deserializer.addTrustedPackages("org.hh99.reservation.dto");

		return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, ReservationDto> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, ReservationDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}
}