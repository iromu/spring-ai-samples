package org.iromu.ai.controller;

import lombok.extern.slf4j.Slf4j;
import org.iromu.ai.model.ChatRequest;
import org.iromu.ai.model.ollama.*;
import org.iromu.ai.service.OllamaService;
import org.iromu.ai.utils.RequestUtils;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


/**
 * The DefaultOllamaController implements the OllamaController interface and provides
 * REST endpoints for managing chat operations, retrieving metadata, and model-related
 * information. This controller interacts with the underlying OllamaService to process
 * requests.
 *
 * Constructor:
 * - Initializes the controller with application metadata, model configuration, and the
 *   service dependency.
 *
 * Methods:
 * - getTags: Retrieves a list of tags identifying the current application and model.
 * - getVersion: Provides the version of the application, fetched from build properties.
 * - chat: Handles chat requests by streaming responses from the underlying model service.
 * - chat2: Provides another implementation for streaming chat responses using mock data
 *   for illustrative purposes.
 *
 * Logging:
 * - Uses various log levels (debug, info, trace) to track method executions and output
 *   details of operations.
 */
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
        log.info("Chat request tokens: {}", request.countTotalTokens());

        List<Message> messages = RequestUtils.getMessages(request);
        Optional<ChatOptions> options = RequestUtils.getOptions(request);

        String model = request.model().replace(appName + "/", "");
        return ollamaService.stream(request, messages, options, model).map(chatResponse -> {

            String message = chatResponse.getResult().getOutput().getText();
            Instant createdAt = chatResponse.getMetadata().get("created-at");
            Integer promptEvalCount = chatResponse.getMetadata().get("prompt-eval-count");
            Integer evalCount = chatResponse.getMetadata().get("eval-count");

            return new ChatStreamResponse.Builder()
                    .model(request.model())
                    .createdAt(createdAt.toString())
                    .message(new ChatMessage(MessageType.ASSISTANT.getValue(), message, null))
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

}
