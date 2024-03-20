package com.wipro;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.wipro.controller.FlightController;
import com.wipro.dto.FareDto;
import com.wipro.dto.FlightDto;
import com.wipro.model.FlightType;
import com.wipro.service.FlightService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@TestInstance(Lifecycle.PER_CLASS)
@WebFluxTest(controllers = FlightController.class)
class BrownfieldAirlinesReactorApplicationTests {
	// RestTemplate
	// WebClient
	// StepVerifier
	@Autowired
	private WebTestClient client;

	@MockBean
	private FlightService flightService;
	List<FlightDto> flights;
	FlightDto flightDto;

	@BeforeEach
	void init() {
		flightDto = FlightDto.builder()
				.id(1L)
				.flightNumber("ABC123")
				.departureAirport("JFK")
				.destinationAirport("LAX")
				.departureTime(LocalDateTime.of(2024, 3, 10, 10, 0))
				.arrivalTime(LocalDateTime.of(2024, 3, 10, 13, 0))
				.availableSeats(150)
				.fareDto(FareDto.builder()
						.amount(200.00)
						.currency("USD")
						.build())
				.type(FlightType.DOMESTIC)
				.build();

		flights = Arrays.asList(FlightDto.builder()
				.id(1L)
				.flightNumber("ABC123")
				.departureAirport("JFK")
				.destinationAirport("LAX")
				.departureTime(LocalDateTime.of(2024, 3, 10, 10, 0))
				.arrivalTime(LocalDateTime.of(2024, 3, 10, 13, 0))
				.availableSeats(150)
				.fareDto(FareDto.builder()
						.amount(200.00)
						.currency("USD")
						.build())
				.type(FlightType.DOMESTIC)
				.build(),
				FlightDto.builder()
						.id(2L)
						.flightNumber("DEF456")
						.departureAirport("LAX")
						.destinationAirport("JFK")
						.departureTime(LocalDateTime.of(2024, 3, 11, 11, 0))
						.arrivalTime(LocalDateTime.of(2024, 3, 11, 14, 0))
						.availableSeats(200)
						.fareDto(FareDto.builder()
								.amount(250.00)
								.currency("USD")
								.build())
						.type(FlightType.DOMESTIC)
						.build());

		BDDMockito.given(flightService.getFlights())
				.willReturn(Flux.fromIterable(flights));

		BDDMockito.given(flightService.createFlight(BDDMockito.any()))
				.willReturn(Mono.just(flightDto));

	}

	@Test
	void testGetAllFlights() {
		this.client.get()
				.uri("/api/flights")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus()
				.is2xxSuccessful()
				.returnResult(FlightDto.class);
	}

	@Test
	void testGetAllFlights2() {
		this.client.get()
				.uri("/api/flights")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus()
				.is2xxSuccessful()
				.expectBody()
				.jsonPath("$.[0].departureAirport")
				.isEqualTo("JFK");
	}

	@Test
	void testPostFlight() {
		this.client.post()
				.uri("/api/flights")
				.accept(MediaType.APPLICATION_JSON)
				.bodyValue(flightDto)
				.exchange()
				.expectStatus()
				.isCreated()
				.expectHeader()
				.location("/api/flights/1");
	}
}
