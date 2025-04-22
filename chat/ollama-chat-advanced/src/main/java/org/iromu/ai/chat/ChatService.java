package org.iromu.ai.chat;

import lombok.extern.slf4j.Slf4j;
import org.iromu.ai.model.ChatRequest;
import org.iromu.ai.service.OllamaService;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;

@Service
@Slf4j
class ChatService implements OllamaService {


    private final Chatbot chatbot;
    private final String model;

    ChatService(Chatbot chatbot, @Value("${spring.ai.ollama.chat.options.model}") String model) {
        this.chatbot = chatbot;
        this.model = model;
    }


    public Flux<ChatResponse> stream(ChatRequest request) {
        Map<String, String> userMessage = request.messages().getLast();
        return chatbot.stream("1", userMessage.get("content"));
    }

}
