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

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public interface OllamaController {

    @GetMapping("/tags")
    Mono<TagsResponse> getTags();

    @GetMapping("/version")
    default Mono<VersionResponse> getVersion() {
        return Mono.just(new VersionResponse("0.0.0"));
    }

    @PostMapping(value = "/chat", produces = MediaType.APPLICATION_NDJSON_VALUE)
    Flux<ChatStreamResponse> chat(@RequestBody ChatRequest request);

    @DeleteMapping("/delete")
    default Mono<Map<String, String>> delete(@RequestBody ModelActionRequest request) {
        return Mono.error(new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "This endpoint is not implemented yet"));

    }

    @PostMapping("/pull")
    default Flux<ActionResponse> pull(@RequestBody ModelActionRequest request) {
        return Flux.error(new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "This endpoint is not implemented yet"));

    }

    @PostMapping("/push")
    default Flux<ActionResponse> push(@RequestBody ModelActionRequest request) {
        return Flux.error(new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "This endpoint is not implemented yet"));

    }

    @GetMapping("/show")
    default Mono<ModelShowResponse> show(@RequestParam String name) {
        return Mono.error(new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "This endpoint is not implemented yet"));

    }
}
