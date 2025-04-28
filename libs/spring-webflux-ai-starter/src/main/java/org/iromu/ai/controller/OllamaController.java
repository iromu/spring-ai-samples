package org.iromu.ai.controller;

import org.iromu.ai.model.ChatRequest;
import org.iromu.ai.model.ollama.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Interface OllamaController represents a REST controller defining endpoints for various
 * operations related to chat models, versioning, and tag management. It acts as a
 * contract for implementing controllers that interact with model-based chat services.
 *
 * The controller provides APIs for interacting with chat models, retrieving metadata,
 * managing model operations (e.g., pull, push, delete), and retrieving model-specific
 * information.
 *
 * Annotations: - @RestController defines this class as a RESTful controller component.
 * - @CrossOrigin enables Cross-Origin Resource Sharing (CORS) for all origins.
 * - @RequestMapping("/api") sets the base path for all endpoints.
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public interface OllamaController {

	/**
	 * Retrieves tags associated with models.
	 * @return a {@code Mono<TagsResponse>} containing a list of model tags wrapped in a
	 * reactive stream.
	 */
	@GetMapping("/tags")
	Mono<TagsResponse> getTags();

	/**
	 * Retrieves the current version of the application.
	 * @return a {@code Mono<VersionResponse>} containing the version information wrapped
	 * in a reactive stream.
	 */
	@GetMapping("/version")
	default Mono<VersionResponse> getVersion() {
		return Mono.just(new VersionResponse("0.0.0"));
	}

	/**
	 * Handles chat requests by processing the provided chat data and returning a stream
	 * of chat responses.
	 * @param request the {@code ChatRequest} containing the input model, messages, stream
	 * configuration, system context, and additional options for the chat interaction.
	 * @return a {@code Flux<ChatStreamResponse>} that streams the response messages from
	 * the chat interaction.
	 */
	@PostMapping(value = "/chat", produces = MediaType.APPLICATION_NDJSON_VALUE)
	Flux<ChatStreamResponse> chat(@RequestBody ChatRequest request);

	/**
	 * Deletes a specified model or resource based on the given request.
	 * @param request the {@code ModelActionRequest} containing details such as the model
	 * name and associated parameters required for the delete operation.
	 * @return a {@code Mono<Map<String, String>>} signaling the result of the operation.
	 * By default, this method returns an error indicating that the endpoint is not
	 * implemented.
	 */
	@DeleteMapping("/delete")
	default Mono<Map<String, String>> delete(@RequestBody ModelActionRequest request) {
		return Mono
			.error(new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "This endpoint is not implemented yet"));

	}

	/**
	 * Executes a pull operation for a specified model or resource.
	 * @param request the {@code ModelActionRequest} containing details such as the model
	 * name and associated parameters required for the pull operation.
	 * @return a {@code Flux<ActionResponse>} that streams the status and progress of the
	 * pull operation. By default, this method returns an error indicating that the
	 * endpoint is not implemented.
	 */
	@PostMapping("/pull")
	default Flux<ActionResponse> pull(@RequestBody ModelActionRequest request) {
		return Flux
			.error(new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "This endpoint is not implemented yet"));

	}

	/**
	 * Executes a push operation for a specified model or resource.
	 * @param request the {@code ModelActionRequest} containing details such as the model
	 * name and associated parameters required for the push operation.
	 * @return a {@code Flux<ActionResponse>} that streams the status and progress of the
	 * push operation. By default, this method returns an error indicating that the
	 * endpoint is not implemented.
	 */
	@PostMapping("/push")
	default Flux<ActionResponse> push(@RequestBody ModelActionRequest request) {
		return Flux
			.error(new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "This endpoint is not implemented yet"));

	}

	/**
	 * Retrieves information about a specific model based on the provided name.
	 * @param name the name of the model whose information is to be retrieved
	 * @return a {@code Mono<ModelShowResponse>} containing the model details wrapped in a
	 * reactive stream
	 */
	@GetMapping("/show")
	default Mono<ModelShowResponse> show(@RequestParam String name) {
		return Mono
			.error(new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "This endpoint is not implemented yet"));

	}

}
