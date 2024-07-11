package com.example.ollama.rag.redis;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    private final Chatbot chatbot;

    public ChatController(Chatbot chatbot) {
        this.chatbot = chatbot;
    }

    @GetMapping("/api/chat")
    public Flux<String> generateStream(@RequestParam(value = "message", defaultValue = "Who are you and what should I know about the transition to consumer direct care network washington?") String message) {
        return chatbot.stream(message).map(chat -> chat.getResult().getOutput().getContent());
    }

}
