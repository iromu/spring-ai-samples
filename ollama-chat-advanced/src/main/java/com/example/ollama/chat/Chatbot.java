package com.example.ollama.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
class Chatbot {
    private final ChatClient chatClient;
    private final ChatClient.Builder chatClientBuilder;
    private final Map<String, List<Message>> history = new HashMap<>();

    Chatbot(ChatModel chatModel) {
        ChatOptions options = OllamaOptions.builder().temperature(0.1).build();
        chatClientBuilder = ChatClient.builder(chatModel).defaultOptions(options);
        this.chatClient = chatClientBuilder.build();
    }

    public Flux<ChatResponse> stream(String id, String message) {

        UserMessage userMessage = new UserMessage(message);

        if (!history.containsKey(id)) {
            log.info("new history for {}", id);
            ArrayList<Message> value = new ArrayList<>();
            value.add(userMessage);
            history.put(id, value);
            final String[] response = {""};
            return chatClient.prompt(new Prompt(userMessage)).stream().chatResponse()
                    .doOnNext(chat -> response[0] += chat.getResult().getOutput().getText())
                    .doFinally(f -> history.get(id).add(new AssistantMessage(response[0])));
        } else {
            List<Message> messages = history.get(id);
            Query query = Query.builder()
                    .text(message)
                    .history(messages)
                    .build();

            QueryTransformer queryTransformer = CompressionQueryTransformer.builder()
                    .chatClientBuilder(chatClientBuilder)
                    .build();

            Mono<Query> queryMono = Mono.fromCallable(() -> {
                // Blocking call here
                return queryTransformer.transform(query);
            }).subscribeOn(Schedulers.boundedElastic());

            return queryMono.flatMapMany(transformedQuery -> {
                log.info("{}", transformedQuery.text());
                final String[] response = {""};
                return chatClient.prompt(transformedQuery.text()).stream().chatResponse()
                        .doOnNext(chat -> response[0] += chat.getResult().getOutput().getText())
                        .doFinally(f -> {
                            history.get(id).add(userMessage);
                            history.get(id).add(new AssistantMessage(response[0]));
                        });
            });
        }


    }
}
