package org.iromu.ai.service;

import org.iromu.ai.model.ChatRequest;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

public interface OllamaService {
    Flux<ChatResponse> stream(ChatRequest request);
}
