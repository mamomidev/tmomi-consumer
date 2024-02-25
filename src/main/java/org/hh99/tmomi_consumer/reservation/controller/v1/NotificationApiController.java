package org.hh99.tmomi_consumer.reservation.controller.v1;

import java.io.IOException;

import org.hh99.tmomi_consumer.reservation.service.EmitterService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
	public SseEmitter stream(
		Authentication authentication,
		@RequestHeader(value = "Last-Event-Id", required = false, defaultValue = "") String lastEventId,
		@RequestHeader(value = "Event-Time-Id", required = false, defaultValue = "") Long eventTimeId) {
		String email = authentication.getName();

		return emitterService.addEmitter(email, lastEventId, eventTimeId);
	}
}