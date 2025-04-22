package org.iromu.ai.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "*") // Allow frontend access
@Slf4j
class ChatController {

    private static final String EXAMPLE = """
            Try these prompts:

            """;
    private final Chatbot chatbot;
    private final String model;

    public ChatController(Chatbot chatbot, @Value("${spring.ai.ollama.chat.options.model}") String model) {
        this.chatbot = chatbot;
        this.model = model;
    }

    @GetMapping("api/chat/info")
    Mono<String> generate() {
        return Mono.just("MCP OpenAPI Chat. Using " + model + "\n" + EXAMPLE);
    }

    @RequestMapping(value = "api/chat", method = {RequestMethod.POST})
    Flux<String> generateStream(@RequestBody(required = false) Input messageBody) {
        log.info(messageBody.message);
        return chatbot.stream(messageBody.message).map(chat -> {
            return chat.getResult().getOutput().getText();
        });
    }

    record Input(String message) {
    }
}
