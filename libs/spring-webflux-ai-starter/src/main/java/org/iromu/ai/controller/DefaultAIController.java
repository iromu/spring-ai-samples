// package org.iromu.ai.controller;
//
// import org.iromu.ai.model.ChatRequest;
// import org.iromu.ai.model.openai.ChatStreamResponse;
// import reactor.core.publisher.Flux;
//
// import java.time.Duration;
// import java.time.Instant;
// import java.util.UUID;
//
// public class DefaultAIController implements OpenAIController {
//
// @Override
// public Flux<ChatStreamResponse> chatCompletions(ChatRequest request) {
// String id = "chatcmpl-" + UUID.randomUUID();
// long created = Instant.now().getEpochSecond();
// String model = request.model() != null ? request.model() : "gpt-3.5-turbo";
//
// // Extract last user message
// String userPrompt = request.messages()
// .stream()
// .filter(m -> "user".equalsIgnoreCase(m.role()))
// .reduce((first, second) -> second)
// .map(ChatRequest.Message::content)
// .orElse("Hi");
//
// String responseText = "Sure! Here's a mock response to: \"" + userPrompt + "\"";
// String[] tokens = responseText.split(" ");
//
// // Step 1: Role delta
// ChatStreamResponse roleChunk = new ChatStreamResponse(id, "chat.completion.chunk",
// created, model,
// new ChatStreamResponse.Choice[] {
// new ChatStreamResponse.Choice(new ChatStreamResponse.Delta("assistant", null), 0, null)
// });
//
// // Step 2: Token chunks
// Flux<ChatStreamResponse> contentChunks = Flux.range(0, tokens.length)
// .map(i -> new ChatStreamResponse(id, "chat.completion.chunk", created, model,
// new ChatStreamResponse.Choice[] { new ChatStreamResponse.Choice(
// new ChatStreamResponse.Delta(null, tokens[i] + " "), 0, null) }))
// .delayElements(Duration.ofMillis(80));
//
// // Step 3: Finish signal
// ChatStreamResponse doneChunk = new ChatStreamResponse(id, "chat.completion.chunk",
// created, model,
// new ChatStreamResponse.Choice[] {
// new ChatStreamResponse.Choice(new ChatStreamResponse.Delta(null, null), 0, "stop") });
//
// return Flux.concat(Flux.just(roleChunk), contentChunks, Flux.just(doneChunk));
// }
//
// }
