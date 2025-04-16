package com.example.ollama.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "*")
@Slf4j
class ChatController {


    private final Chatbot chatbot;

    ChatController(Chatbot chatbot) {
        this.chatbot = chatbot;
    }

    @GetMapping("api/chat/info")
    Mono<String> generate() {
        return Mono.just("Advanced chat with history");
    }

    @RequestMapping(value = "api/chat", method = {RequestMethod.POST})
    Flux<String> generateStream(@RequestBody(required = false) Input messageBody) {
        return chatbot.stream(messageBody.id, messageBody.message).map(chat -> chat.getResult().getOutput().getText());
    }

    record Input(String id, String message) {
    }
}
