package org.hh99.tmomi_consumer.reservation.controller.v1;

import org.hh99.tmomi_consumer.reservation.dto.ReservationDto;
import org.hh99.tmomi_consumer.reservation.service.ReservationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReservationController {

	private final ReservationService reservationService;

	@PostMapping("/eventTimes/reservation")
	public void reservation(@RequestBody ReservationDto reservationDto) {
		reservationService.sendReservationEventId(reservationDto);
	}
}
