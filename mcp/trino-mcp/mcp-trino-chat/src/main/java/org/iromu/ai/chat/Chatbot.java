package org.iromu.ai.chat;

import io.modelcontextprotocol.client.McpAsyncClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.mcp.AsyncMcpToolCallbackProvider;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

@Component
@Slf4j
class Chatbot {

    private final ChatClient chatClient;
    private final ChatClient.Builder chatClientBuilder;

    Chatbot(ChatModel chatModel, List<McpAsyncClient> mcpClients) {
        for (McpAsyncClient mcpClient : mcpClients) {
            log.info("Registering MCP Client {} v{}, Server {} v{}",
                    mcpClient.getClientInfo().name(),
                    mcpClient.getClientInfo().version(),
                    mcpClient.getServerInfo().name(),
                    mcpClient.getServerInfo().version()
            );
        }

        var mcpToolProvider = new AsyncMcpToolCallbackProvider(mcpClients);
        MessageChatMemoryAdvisor memoryAdvisor = new MessageChatMemoryAdvisor(new InMemoryChatMemory());
        SimpleLoggerAdvisor simpleLoggerAdvisor = new SimpleLoggerAdvisor();
        chatClientBuilder = ChatClient.builder(chatModel)
                .defaultTools(mcpToolProvider)
                .defaultAdvisors(memoryAdvisor, simpleLoggerAdvisor);
        this.chatClient = chatClientBuilder.build();
        log.info("Chatbot built");
    }

    public Flux<ChatResponse> stream(String id, String message) {
        return chatClient.prompt(message)
                .advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, id))
                .stream().chatResponse();
    }

}
