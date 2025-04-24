package org.iromu.ai.service;

import org.iromu.ai.model.ChatRequest;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;

/**
 * Interface OllamaService defines a contract for handling streaming chat interactions.
 * It acts as an abstraction for chat services that process a {@code ChatRequest} and
 * provide a reactive stream of {@code ChatResponse}.
 *
 * Implementations of this interface are expected to process chat requests and return
 * responses in a non-blocking, reactive manner using Project Reactor's {@code Flux}.
 *
 * Responsibilities:
 * - Provides a method for streaming chat responses based on an input request.
 * - Supports reactive processing for scalable and efficient handling of chat interactions.
 */
public interface OllamaService {
    /**
     * Streams chat responses for a given chat request, enabling reactive handling of message generation.
     *
     * @param request the {@code ChatRequest} containing details such as the model to use, a list of messages,
     *                streaming preferences, system context, and optional parameters for processing the chat interaction.
     * @return a {@code Flux<ChatResponse>} representing a reactive stream of generated chat responses.
     */
    Flux<ChatResponse> stream(ChatRequest request, List<Message> messages, Optional<ChatOptions> options, String model);
}
