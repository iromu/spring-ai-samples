package org.iromu.ai.chat;

import io.modelcontextprotocol.client.McpAsyncClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.mcp.AsyncMcpToolCallbackProvider;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
@Slf4j
class Chatbot {

	private final ChatClient chatClient;

	Chatbot(ChatModel chatModel, List<McpAsyncClient> mcpClients) {
		for (McpAsyncClient mcpClient : mcpClients) {
			log.info("Registering MCP Client {} v{}, Server {} v{}", mcpClient.getClientInfo().name(),
					mcpClient.getClientInfo().version(), mcpClient.getServerInfo().name(),
					mcpClient.getServerInfo().version());
		}

		var mcpToolProvider = new AsyncMcpToolCallbackProvider(mcpClients);
		MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor
			.builder(MessageWindowChatMemory.builder().build())
			.build();
		ChatClient.Builder chatClientBuilder = ChatClient.builder(chatModel)
				.defaultToolCallbacks(mcpToolProvider)
			.defaultAdvisors(memoryAdvisor);
		this.chatClient = chatClientBuilder.build();
		log.info("Chatbot built");
	}

	public Flux<ChatResponse> stream(String id, List<Message> messages) {
		return chatClient.prompt(messages.getLast().getText())
			.advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, id))
			.stream()
			.chatResponse();
	}

}
