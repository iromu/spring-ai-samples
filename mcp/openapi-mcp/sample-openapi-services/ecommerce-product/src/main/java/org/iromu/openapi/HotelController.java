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
@RequestMapping("hotel")
@Slf4j
public class HotelController {

	@GetMapping("/destination")
	@Operation(description = "Get hotels for a given destination")
	public List<Hotel> getHotelsByLocation(@Parameter(description = "Destination where hotels are located",
			required = true) @RequestParam("destination") String destination) {

		log.info("Fetching hotels for destination: {}", destination);

		return List.of(new Hotel("2804", "Hotel Castilla", "Urban hotel located in Madrid"),
				new Hotel("3804", "Hotel Barajas", "Urban hotel located in Madrid"));
	}

}
