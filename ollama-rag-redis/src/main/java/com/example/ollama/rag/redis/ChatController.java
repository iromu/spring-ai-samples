package com.example.ollama.rag.redis;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@CrossOrigin(origins = "*") // Allow frontend access
class ChatController {

    private final Chatbot chatbot;

    public ChatController(Chatbot chatbot) {
        this.chatbot = chatbot;
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
