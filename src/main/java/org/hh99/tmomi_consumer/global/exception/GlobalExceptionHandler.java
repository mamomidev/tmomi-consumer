package org.hh99.tmomi_consumer.global.exception;

import org.hh99.tmomi.global.exception.GlobalException;
import org.hh99.tmomi.global.exception.message.dto.ExceptionCodeDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(GlobalException.class)
	public ResponseEntity<ExceptionCodeDto> handleGlobalException(GlobalException ex) {
		return ResponseEntity.status(ex.getHttpStatus())
			.body(ExceptionCodeDto.builder()
				.code(ex.getExceptionCode().name())
				.message(ex.getExceptionCode().getMessage())
				.build());
	}
}