package org.hh99.tmomi_consumer.websocket;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

	private static final ConcurrentHashMap<String, WebSocketSession> CLIENTS = new ConcurrentHashMap<String, WebSocketSession>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		CLIENTS.put(session.getId(), session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		CLIENTS.remove(session.getId());
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		System.out.println(message.getPayload());

		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "dynamic-group");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Collections.singleton("reservationEventTimeId" + message.getPayload()));

		while (true) {
			ConsumerRecords<String, String> record = consumer.poll(Duration.ofMillis(100));
			record.forEach(r -> {
				System.out.println("Received message from " + r.topic() + " : " + r.value() + " :" + r.offset());
				// 메시지 처리 로직을 여기에 추가

				/*
					1. 같은 아이디 일 경우에만 메시지를 전달
					2. 남은 좌석을 보여주고 CLIENTS.remove(session.getId());로 연결을 끊음
				 */
				
				String id = session.getId();  //메시지를 보낸 아이디
				CLIENTS.entrySet().forEach(arg -> {
					if (!arg.getKey().equals(id)) {  //같은 아이디가 아니면 메시지를 전달합니다.
						try {
							arg.getValue()
								.sendMessage(new TextMessage(
									"Received message from " + r.topic() + ": " + r.value() + " :" + r.offset()));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
			});
		}
	}
}