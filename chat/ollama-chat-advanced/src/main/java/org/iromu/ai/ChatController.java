package org.iromu.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "*")
@Slf4j
class ChatController {


    private final Chatbot chatbot;
    private final String model;

    ChatController(Chatbot chatbot, @Value("${spring.ai.ollama.chat.options.model}") String model) {
        this.chatbot = chatbot;
        this.model = model;
    }

    @GetMapping("api/chat/info")
    Mono<String> generate() {
        return Mono.just("Advanced chat with history. Using " + model);
    }

    @RequestMapping(value = "api/chat", method = {RequestMethod.POST})
    Flux<String> generateStream(@RequestBody(required = false) Input messageBody) {
        return chatbot.stream(messageBody.id, messageBody.message).map(chat -> chat.getResult().getOutput().getText());
    }

    record Input(String id, String message) {
    }
}
