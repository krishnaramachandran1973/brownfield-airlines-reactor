package com.wipro.service;

import org.springframework.stereotype.Service;

import com.wipro.dto.FlightDto;
import com.wipro.exceptions.FlightCannotBeSavedException;
import com.wipro.exceptions.FlightNotFoundException;
import com.wipro.mapper.DataMapper;
import com.wipro.model.Flight;
import com.wipro.repository.FlightRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@RequiredArgsConstructor
@Service
public class FlightService {
	private final FlightRepository repository;

	public Flux<FlightDto> getFlights() {
		log.info("getFlights");
		return Flux.fromStream(this.repository.findAll()
				.stream())
				.log()
				.map(DataMapper::toFlightDto)
				.switchIfEmpty(Flux.error(() -> new FlightNotFoundException("No flights found")))
				.subscribeOn(Schedulers.boundedElastic());
	}

	public Mono<FlightDto> findFlightById(Long id) {
		return Mono.justOrEmpty(this.repository.findById(id))
				.map(DataMapper::toFlightDto)
				.switchIfEmpty(Mono.error(() -> new FlightNotFoundException("No flight with id " + id + " found")))
				.subscribeOn(Schedulers.boundedElastic());
	}

	public Mono<FlightDto> createFlight(FlightDto dto) {
		return Mono.just(DataMapper.toFlight(dto))
				.map(toSaveFlight -> this.repository.save(toSaveFlight))
				.map(savedFlight -> DataMapper.toFlightDto(savedFlight))
				.onErrorResume(ex -> Mono.error(() -> new FlightCannotBeSavedException(ex.getMessage())))
				.subscribeOn(Schedulers.boundedElastic());
	}

	public Mono<Void> updateFlight(FlightDto flightDto) {
		return Mono.justOrEmpty(this.repository.findById(flightDto.getId()))
				.map(savedFlight -> {
					Flight toSaveFlight = DataMapper.toFlight(flightDto);
					toSaveFlight.setId(flightDto.getId());
					return this.repository.save(toSaveFlight);
				})
				.switchIfEmpty(Mono.error(() -> new FlightNotFoundException(
						"No flight with id " + flightDto.getId() + " found for updation")))
				.then()
				.subscribeOn(Schedulers.boundedElastic());
	}

}
