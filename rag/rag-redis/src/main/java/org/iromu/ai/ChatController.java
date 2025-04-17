package org.iromu.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "*") // Allow frontend access
class ChatController {

    private final Chatbot chatbot;
    private final String model;

    public ChatController(Chatbot chatbot, @Value("${spring.ai.ollama.chat.options.model}") String model) {
        this.chatbot = chatbot;
        this.model = model;
    }

    @GetMapping("api/chat/info")
    Mono<String> generate() {
        return Mono.just("Simple RAG. Using " + model);
    }

    @RequestMapping(value = "api/chat", method = {RequestMethod.GET, RequestMethod.POST})
    Flux<String> generateStream(@RequestParam(value = "message",
                                        defaultValue = "Who are you and what should I know about the transition to consumer direct care network washington?") String message,
                                @RequestBody(required = false) Input messageBody) {
        String userMessage = messageBody != null ? messageBody.message : message;

        return chatbot.stream(userMessage).map(chat -> chat.getResult().getOutput().getText());
    }

    record Input(String message) {
    }
}
