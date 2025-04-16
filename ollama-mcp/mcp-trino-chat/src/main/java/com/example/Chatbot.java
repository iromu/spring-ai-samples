package com.example;

import io.modelcontextprotocol.client.McpSyncClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
@Slf4j
class Chatbot {

    private final ChatClient chatClient;
    private final ChatClient.Builder chatClientBuilder;

    Chatbot(ChatModel chatModel, List<McpSyncClient> mcpClients) {
        var mcpToolProvider = new SyncMcpToolCallbackProvider(mcpClients);
        chatClientBuilder = ChatClient.builder(chatModel).defaultTools(mcpToolProvider);
        this.chatClient = chatClientBuilder.build();
    }

    public Flux<ChatResponse> stream(String message) {
        return chatClient.prompt(message).stream().chatResponse();
    }

}
