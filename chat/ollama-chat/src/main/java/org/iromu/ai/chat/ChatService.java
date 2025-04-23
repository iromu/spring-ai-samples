package org.iromu.ai.chat;

import lombok.extern.slf4j.Slf4j;
import org.iromu.ai.model.ChatRequest;
import org.iromu.ai.service.OllamaService;
import org.iromu.ai.utils.RequestUtils;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
class ChatService implements OllamaService {

    private final ChatModel chatModel;

    public ChatService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public Flux<ChatResponse> stream(ChatRequest request) {
        Prompt prompt = new Prompt(RequestUtils.getMessages(request));
        return chatModel.stream(prompt);
    }

}
