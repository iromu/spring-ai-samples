package org.iromu.ai.gateway;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.iromu.ai.model.ollama.TagsResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for discovering metadata tags from a list of configured URLs.
 *
 * The TagDiscoveryService periodically fetches metadata tags from external services
 * configured via the {@code AiConfig} component. These tags are fetched using a
 * non-blocking, reactive approach and are made available through a reactive stream.
 *
 * This service is initialized with a discovery loop that runs at a fixed interval,
 * polling the configured endpoints to retrieve metadata tags.
 *
 * Core responsibilities: - Fetch metadata tags from external services at regular
 * intervals. - Provide real-time access to discovered tags via a reactive stream. -
 * Handle errors gracefully and return default responses when remote services fail.
 *
 * Dependencies: - Spring's {@link WebClient.Builder} for making HTTP requests. -
 * {@link AiConfig} to retrieve a list of target URLs for tag discovery.
 *
 * Key Methods: - {@code startDiscoveryLoop}: Initializes the discovery loop that executes
 * at fixed intervals. - {@code discoverOnce}: Performs a one-time fetch of metadata tags
 * from all configured URLs.
 *
 * Records: - {@code MetaTagResponse}: Represents a response containing metadata tags
 * retrieved from a specific URL along with the associated response details.
 */
@Service
@Slf4j
public class TagDiscoveryService {

	private final List<String> urls;

	private final WebClient.Builder webClientBuilder;

	@Getter
	private Flux<MetaTagResponse> tags;

	public TagDiscoveryService(AiConfig aiConfig, WebClient.Builder webClientBuilder) {
		this.urls = aiConfig.getUrls();
		this.webClientBuilder = webClientBuilder;
	}

	@PostConstruct
	public void startDiscoveryLoop() {
		Flux.interval(Duration.ZERO, Duration.ofSeconds(10))
			.flatMap(tick -> discoverOnce())
			.subscribeOn(Schedulers.boundedElastic())
			.subscribe();
	}

	private Flux<MetaTagResponse> discoverOnce() {
		log.info("Discovering tags from {}", urls);
		this.tags = Flux.fromIterable(urls).flatMap(url -> {
			log.debug("Discovering tags from {}/api/tags", url);
			return webClientBuilder.baseUrl(url)
				.build()
				.get()
				.uri("/api/tags")
				.accept(MediaType.APPLICATION_JSON)
				.exchangeToMono(response -> response.bodyToMono(TagsResponse.class))
				.onErrorReturn(new TagsResponse(new ArrayList<>()))
				.map(tagsResponse -> new MetaTagResponse(url, tagsResponse));
		}).cache();

		return this.tags;
	}

	public record MetaTagResponse(String url, TagsResponse tagsResponse) {
	}

}
