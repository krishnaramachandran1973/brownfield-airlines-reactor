package com.wipro.dto;

import java.time.LocalDateTime;

import com.wipro.model.FlightType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightDto {

	private Long id;
	@NotNull
	@Size(
			min = 3,
			max = 15,
			message = "Title must be minimum 3 characters, and maximum 15 characters long")
	private String flightNumber;
	@NotNull
	@NotEmpty
	private String departureAirport;
	@NotNull
	@NotEmpty
	private String destinationAirport;
	@NotNull
	private LocalDateTime departureTime;
	@NotNull
	private LocalDateTime arrivalTime;
	@NotNull
	private int availableSeats;
	@Default
	private FareDto fareDto = new FareDto();
	@NotNull
	private FlightType type;
}
