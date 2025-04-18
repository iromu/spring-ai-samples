package org.iromu.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "*")
@Slf4j
class ChatController {

    private final ChatModel chatModel;
    private final String model;

    public ChatController(ChatModel chatModel, @Value("${spring.ai.ollama.chat.options.model}") String model) {
        this.chatModel = chatModel;
        this.model = model;
    }

    @GetMapping("api/request")
    Mono<String> generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Mono.fromCallable(() -> chatModel.call(message));
    }

    @GetMapping("api/chat/info")
    Mono<String> generate() {
        return Mono.just("Simple chat. Using " + model);
    }

    @RequestMapping(value = "api/chat", method = {RequestMethod.GET, RequestMethod.POST})
    Flux<String> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message,
                                @RequestBody(required = false) Input messageBody) {
        String userMessage = messageBody != null ? messageBody.message : message;
        log.info("User: {}", userMessage);
        Prompt prompt = new Prompt(new UserMessage(userMessage));
        return chatModel.stream(prompt).map(chat -> {
            // log.info("{}", chat.getResult().getOutput().getText());
            return chat.getResult().getOutput().getText();
        });
    }

    record Input(String message) {
    }
}
