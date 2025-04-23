package org.iromu.ai.chat;

import lombok.extern.slf4j.Slf4j;
import org.iromu.ai.model.ChatRequest;
import org.iromu.ai.service.OllamaService;
import org.iromu.ai.utils.RequestUtils;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
class ChatService implements OllamaService {


    private final Chatbot chatbot;

    ChatService(Chatbot chatbot, @Value("${spring.ai.ollama.chat.options.model}") String model) {
        this.chatbot = chatbot;
    }


    public Flux<ChatResponse> stream(ChatRequest request) {
        return chatbot.stream("1", RequestUtils.getMessages(request));
    }

}
