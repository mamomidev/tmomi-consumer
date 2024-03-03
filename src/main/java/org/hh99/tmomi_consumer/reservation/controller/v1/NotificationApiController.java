package org.hh99.tmomi_consumer.reservation.controller.v1;

import org.hh99.tmomi_consumer.reservation.service.EmitterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class NotificationApiController {
	private final EmitterService emitterService;

	@GetMapping(value = "/api/v1/sse-connection", produces = "text/event-stream")
	public SseEmitter stream(@RequestBody String email) {
		return emitterService.addEmitter(email);
	}
}
