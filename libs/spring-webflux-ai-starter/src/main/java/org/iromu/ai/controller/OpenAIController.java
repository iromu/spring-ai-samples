package org.iromu.ai.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import org.iromu.ai.model.ChatRequest;
import org.iromu.ai.model.openai.ChatStreamResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * OpenAI endpoints
 * <p>
 * <pre>
	 GET /v1/models
	 GET /v1/models/{namespace}/{name}
	 POST /v1/chat/completions
	 POST /v1/completions
	 POST /v1/embeddings
 * </pre>
 */
@RestController
@Slf4j
@RequestMapping("/v1")
public class OpenAIController {

	// Chat Completions Endpoint
	@PostMapping(value = "/chat/completions", produces = MediaType.APPLICATION_NDJSON_VALUE)
	public Mono<?> chatCompletion(@RequestBody ChatCompletionRequest request) {
		if (Boolean.TRUE.equals(request.getStream())) {
			return Mono.just(Flux.interval(Duration.ofMillis(500))
				.take(request.getMaxTokens())
				.map(i -> new ChatChunk("assistant", "Token " + i))
				.map(ChatCompletionChunk::new));
		}
		else {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < request.getMaxTokens(); i++) {
				sb.append("Token ").append(i).append(" ");
			}
			ChatMessage message = new ChatMessage("assistant", sb.toString().trim());
			return Mono.just(new ChatCompletionResponse(List.of(message)));
		}
	}

	// Text Completions Endpoint
	@PostMapping("/completions")
	public Mono<TextCompletionResponse> textCompletion(@RequestBody TextCompletionRequest request) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < request.getMaxTokens(); i++) {
			sb.append("Token ").append(i).append(" ");
		}
		return Mono.just(new TextCompletionResponse(List.of(sb.toString().trim())));
	}

	// Embeddings Endpoint
	@PostMapping("/embeddings")
	public Mono<EmbeddingResponse> embeddings(@RequestBody EmbeddingRequest request) {
		return Mono.just(new EmbeddingResponse(List.of(new float[] { 0.1f, 0.2f, 0.3f })));
	}

	// Image Generation Endpoint
	@PostMapping("/images/generations")
	public Mono<ImageGenerationResponse> imageGeneration(@RequestBody ImageGenerationRequest request) {
		return Mono.just(new ImageGenerationResponse(List.of("https://fake.image.url/image1.png")));
	}

	// Audio Transcriptions Endpoint
	@PostMapping(value = "/audio/transcriptions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Mono<AudioTranscriptionResponse> audioTranscription() {
		return Mono.just(new AudioTranscriptionResponse("Transcribed text here."));
	}

	// Moderation Endpoint
	@PostMapping("/moderations")
	public Mono<ModerationResponse> moderation(@RequestBody ModerationRequest request) {
		return Mono.just(new ModerationResponse(false));
	}

	// DTOs Below
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	static class ChatCompletionRequest {

		private List<ChatMessage> messages;

		private Boolean stream;

		private int maxTokens = 5;

	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	static class ChatMessage {

		private String role;

		private String content;

	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	static class ChatCompletionResponse {

		private List<ChatMessage> choices;

	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	static class ChatChunk {

		private String role;

		private String delta;

	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	static class ChatCompletionChunk {

		private ChatChunk choices;

	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	static class TextCompletionRequest {

		private String prompt;

		private int maxTokens = 5;

	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	static class TextCompletionResponse {

		private List<String> choices;

	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	static class EmbeddingRequest {

		private String input;

	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	static class EmbeddingResponse {

		private List<float[]> data;

	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	static class ImageGenerationRequest {

		private String prompt;

		private int n = 1;

	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	static class ImageGenerationResponse {

		private List<String> data;

	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	static class AudioTranscriptionResponse {

		private String text;

	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	static class ModerationRequest {

		private String input;

	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	static class ModerationResponse {

		private boolean flagged;

	}

}
