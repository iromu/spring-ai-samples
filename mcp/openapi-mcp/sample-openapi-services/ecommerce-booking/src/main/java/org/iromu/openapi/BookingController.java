package org.iromu.openapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("booking")
@Slf4j
public class BookingController {

	@GetMapping("/availability")
	@Operation(
			description = "Get the price of booking a room stay in a Hotel for given dates and number of adults and children")
	public List<Availability> calculateHotelBookingPrice(
			@Parameter(description = "Hotel to be booked", required = true) @RequestParam("hotel") String hotel,
			@Parameter(description = "Arrival date to the hotel",
					required = true) @RequestParam("arrival") String arrival,
			@Parameter(description = "Duration of stay in number of nights",
					required = true) @RequestParam("nights") Integer nights,
			@Parameter(description = "Number of adults", required = true) @RequestParam("adults") Integer adults,
			@Parameter(description = "Number of children",
					required = true) @RequestParam("children") Integer children) {
		log.info(
				"Calculating hotel booking price with parameters - hotel: {}, arrival: {}, nights: {}, adults: {}, children: {}",
				hotel, arrival, nights, adults, children);

		return List.of(new Availability("Double room", 120.45), new Availability("Suite room", 148.65));
	}

}
