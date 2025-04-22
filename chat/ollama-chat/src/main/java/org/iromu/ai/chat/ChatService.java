package org.iromu.ai.chat;

import lombok.extern.slf4j.Slf4j;
import org.iromu.ai.model.ChatRequest;
import org.iromu.ai.service.OllamaService;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;

@Service
@Slf4j
class ChatService implements OllamaService {

    private final ChatModel chatModel;
    private final String model;

    public ChatService(ChatModel chatModel, @Value("${spring.ai.ollama.chat.options.model}") String model) {
        this.chatModel = chatModel;
        this.model = model;
    }

    public Flux<ChatResponse> stream(ChatRequest request) {
        Map<String, String> userMessage = request.messages().getLast();
        log.info("User: {}", userMessage);
        Prompt prompt = new Prompt(new UserMessage(userMessage.get("content")));
        return chatModel.stream(prompt);
    }
}
