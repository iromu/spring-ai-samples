package org.iromu.ai.gateway;

import lombok.extern.slf4j.Slf4j;
import org.iromu.ai.controller.OllamaController;
import org.iromu.ai.model.ChatRequest;
import org.iromu.ai.model.ollama.ChatStreamResponse;
import org.iromu.ai.model.ollama.ModelTag;
import org.iromu.ai.model.ollama.TagsResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping({"/api"})
public class OllamaGatewayController implements OllamaController {

    private final WebClient.Builder webClientBuilder;

    private final TagDiscoveryService tagDiscoveryService;

    public OllamaGatewayController(WebClient.Builder webClientBuilder,
                                   TagDiscoveryService tagDiscoveryService) {
        this.webClientBuilder = webClientBuilder;
        this.tagDiscoveryService = tagDiscoveryService;
    }


    private TagsResponse aggregateResponses(List<TagDiscoveryService.MetaTagResponse> responses) {
        List<ModelTag> models = new ArrayList<>();
        for (TagDiscoveryService.MetaTagResponse response : responses) {
            if (response != null && response.tagsResponse().models() != null)
                models.addAll(response.tagsResponse().models());
        }
        log.info("models {}", models);
        return new TagsResponse(models);
    }

    public Mono<TagsResponse> getTags() {
        return this.tagDiscoveryService.getTags().collectList()
                .map(this::aggregateResponses)
                .doOnNext(o -> log.debug("{}", o));
    }

    public Flux<ChatStreamResponse> chat(@RequestBody ChatRequest request) {
        log.info("{}", request);
        ModelTag modelName = new ModelTag(request.model(), null);

        return this.tagDiscoveryService.getTags()
                .filter(t -> t != null && t.tagsResponse().models() != null && t.tagsResponse().models().contains(modelName))
                .flatMap(o -> {
                            log.info("{}/api/chat", o.url());
                            return webClientBuilder.baseUrl(o.url()).build()
                                    .post()
                                    .uri("/api/chat")
                                    .bodyValue(request)
                                    .accept(MediaType.APPLICATION_NDJSON)
                                    .exchangeToFlux(response -> response.bodyToFlux(ChatStreamResponse.class));
                        }

                );

    }

}
