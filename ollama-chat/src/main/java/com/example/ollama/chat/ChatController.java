package com.example.ollama.chat;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
class ChatController {

    private final ChatModel chatModel;

    public ChatController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("api/request")
    Mono<String> generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Mono.fromCallable(() -> chatModel.call(message));
    }

    @GetMapping("api/chat")
    Flux<String> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatModel.stream(prompt).map(chat -> chat.getResult().getOutput().getText());
    }

}
