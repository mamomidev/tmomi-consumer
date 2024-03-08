package org.hh99.tmomi_consumer.emitter.controller.v1;

import org.hh99.reservation.dto.ReservationDto;
import org.hh99.tmomi_consumer.emitter.service.EmitterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class EmitterController {
	private final EmitterService emitterService;

	@GetMapping(value = "/api/v1/sse-connection", produces = "text/event-stream")
	public SseEmitter stream(@PathParam("email") String email, @PathParam("eventTimeId") Long eventTimeId) {
		return emitterService.addEmitter(new ReservationDto(email, eventTimeId));
	}
}
