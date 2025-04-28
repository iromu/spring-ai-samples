package org.iromu.ai.controller;

import org.iromu.ai.model.ChatRequest;
import org.iromu.ai.model.openai.ChatStreamResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/v1")
public interface OpenAIController {

	@PostMapping(value = "/chat/completions", produces = MediaType.APPLICATION_NDJSON_VALUE)
	Flux<ChatStreamResponse> chatCompletions(@RequestBody ChatRequest request);

}
