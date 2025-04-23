package org.iromu.ai.controller;

import lombok.extern.slf4j.Slf4j;
import org.iromu.ai.model.ChatRequest;
import org.iromu.ai.model.ollama.*;
import org.iromu.ai.service.OllamaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;


@Slf4j
public class DefaultOllamaController implements OllamaController {

    private final Optional<BuildProperties> buildProperties;
    private final String appName;
    private final String model;
    private final OllamaService ollamaService;

    public DefaultOllamaController(
            Optional<BuildProperties> buildProperties,
            @Value("${spring.application.name}") String appName,
            @Value("${spring.ai.ollama.chat.options.model}") String model, OllamaService ollamaService
    ) {

        this.buildProperties = buildProperties;
        this.appName = appName;
        this.model = model;
        this.ollamaService = ollamaService;
    }

    @Override
    public Mono<TagsResponse> getTags() {
        ModelTag modelTag = new ModelTag(appName + "/" + model, null);
        log.debug("Default getTags() {}", modelTag);
        return Mono.just(new TagsResponse(
                List.of(
                        modelTag
                )
        ));
    }

    @Override
    public Mono<VersionResponse> getVersion() {
        String version = buildProperties.isPresent() ? buildProperties.get().getVersion() : "0.0.0";
        log.info("Default getVersion() {}", version);
        return Mono.just(new VersionResponse(version));
    }

    @Override
    public Flux<ChatStreamResponse> chat(@RequestBody ChatRequest request) {
        log.trace("Default chat {}", request);

        return ollamaService.stream(request).map(chatResponse -> {

            String message = chatResponse.getResult().getOutput().getText();
            Instant createdAt = chatResponse.getMetadata().get("created-at");
            Integer promptEvalCount = chatResponse.getMetadata().get("prompt-eval-count");
            Integer evalCount = chatResponse.getMetadata().get("eval-count");

            return new ChatStreamResponse.Builder()
                    .model(request.model())
                    .createdAt(createdAt.toString())
                    .message(new ChatMessage("assistant", message, null))
                    .done(false)
                    .totalDuration(4883583458L)
                    .loadDuration(1334875L)
                    .promptEvalCount(promptEvalCount)
                    .promptEvalDuration(342546000L)
                    .evalCount(evalCount)
                    .evalDuration(4535599000L)
                    .build();
        });

    }

    public Flux<ChatStreamResponse> chat2(@RequestBody ChatRequest request) {
        log.info("Default chat {}", request);
        String lastMessage = request.messages().getLast().content();
        String modelName = request.model() != null ? request.model() : "gpt-neo:latest";

        // Timestamp for all chunks
        String createdAt = OffsetDateTime.now().toString();

        Flux<ChatStreamResponse> streaming = Flux.range(0, 3)
                .delayElements(Duration.ofMillis(200))
                .map(i -> new ChatStreamResponse.Builder()
                        .model(modelName)
                        .createdAt(createdAt)
                        .message(new ChatMessage("assistant", "chunk " + i, null))
                        .done(false)
                        .build());

        ChatStreamResponse doneResponse = new ChatStreamResponse.Builder()
                .model(modelName)
                .createdAt(OffsetDateTime.now().toString())
                .message(new ChatMessage("assistant", "", null))
                .done(true)
                .totalDuration(4883583458L)
                .loadDuration(1334875L)
                .promptEvalCount(26)
                .promptEvalDuration(342546000L)
                .evalCount(282)
                .evalDuration(4535599000L)
                .build();

        return streaming.concatWith(Mono.just(doneResponse));
    }
}
