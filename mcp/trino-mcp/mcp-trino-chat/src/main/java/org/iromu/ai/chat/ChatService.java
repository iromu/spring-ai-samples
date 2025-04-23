package org.iromu.ai.chat;

import lombok.extern.slf4j.Slf4j;
import org.iromu.ai.model.ChatRequest;
import org.iromu.ai.service.OllamaService;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
class ChatService implements OllamaService {

    private final Chatbot chatbot;

    public ChatService(Chatbot chatbot) {
        this.chatbot = chatbot;
    }

    public Flux<ChatResponse> stream(ChatRequest request) {
        ChatRequest.Message userMessage = request.messages().getLast();
        return chatbot.stream("1", userMessage.content());
    }

}
