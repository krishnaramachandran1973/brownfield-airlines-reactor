package com.wipro.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.util.ForwardedHeaderUtils;

import com.wipro.common.Util;
import com.wipro.dto.FlightDto;
import com.wipro.errors.FlightError;
import com.wipro.exceptions.FlightCannotBeSavedException;
import com.wipro.exceptions.FlightNotFoundException;
import com.wipro.service.FlightService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/flights")
public class FlightController {
	private final FlightService flightService;

	@GetMapping
	public Flux<FlightDto> getFlights() {
		return this.flightService.getFlights();
	}

	@GetMapping("/{id}")
	public Mono<FlightDto> findFlightById(@PathVariable Long id) {
		return this.flightService.findFlightById(id);
	}

	@PostMapping
	public Mono<ResponseEntity<?>> createFlight(@Valid @RequestBody FlightDto flightDto, ServerHttpRequest request) {
		return this.flightService.createFlight(flightDto)
				.map(dto -> ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders())
						.path("/{id}")
						.buildAndExpand(dto.getId())
						.toUri())
				.map(uri -> ResponseEntity.created(uri)
						.build());
	}

	@PutMapping
	public Mono<ResponseEntity<?>> updateFlight(@Valid @RequestBody FlightDto flightDto) {
		return this.flightService.updateFlight(flightDto)
				.then(Mono.just(ResponseEntity.accepted()
						.build()));
	}

	@ExceptionHandler
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public Mono<FlightError> notFound(FlightNotFoundException ex) {
		return Mono.fromSupplier(() -> FlightError.builder()
				.errorCode(ex.getErrorCode())
				.errorMessage(ex.getMessage())
				.build());
	}

	@ExceptionHandler
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public Mono<FlightError> cannotSave(FlightCannotBeSavedException ex) {
		return Mono.fromSupplier(() -> FlightError.builder()
				.errorCode(ex.getErrorCode())
				.errorMessage(ex.getMessage())
				.build());
	}

	@ExceptionHandler
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public Mono<FlightError> cannotValidate(WebExchangeBindException ex) {
		return Mono.fromSupplier(() -> Util.buildError(ex, "Validation failed"));
	}

}
