package org.iromu.ai.chat;

import lombok.extern.slf4j.Slf4j;
import org.iromu.ai.model.ChatRequest;
import org.iromu.ai.service.OllamaService;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
class ChatService implements OllamaService {

	private final Chatbot chatbot;

	ChatService(Chatbot chatbot) {
		this.chatbot = chatbot;
	}

	public Flux<ChatResponse> stream(ChatRequest request, List<Message> messages, Optional<ChatOptions> options,
			String model) {
		return chatbot.stream("1", messages);
	}

}
