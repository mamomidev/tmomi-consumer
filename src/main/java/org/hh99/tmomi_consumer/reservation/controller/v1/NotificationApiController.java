package org.hh99.tmomi_consumer.reservation.controller.v1;

import java.io.IOException;

import org.hh99.tmomi_consumer.reservation.service.EmitterService;
import org.hh99.tmomi_consumer.reservation.service.ReservationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	private final ReservationService reservationService;

	@GetMapping(value = "/api/sse-connection/{email}", produces = "text/event-stream")
	public SseEmitter stream(@PathVariable String email,
		@RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) throws
		IOException {
		return emitterService.addEmitter(email, lastEventId);
	}
}