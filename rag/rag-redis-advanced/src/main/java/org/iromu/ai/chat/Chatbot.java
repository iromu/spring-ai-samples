package org.iromu.ai.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Component
@Slf4j
class Chatbot {
    private final ChatClient chatClient;
    private final ChatClient.Builder chatClientBuilder;

    Chatbot(ChatModel chatModel, VectorStore vectorStore) {
        ChatOptions options = OllamaOptions.builder().temperature(0.1).build();
        chatClientBuilder = ChatClient.builder(chatModel).defaultOptions(options)
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore));
        this.chatClient = chatClientBuilder.build();

    }

    public Flux<ChatResponse> stream(String id, List<Message> messages) {

        Query query = Query.builder()
                .text(messages.getLast().getText())
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
            return chatClient.prompt(transformedQuery.text()).stream().chatResponse();
        });
    }
}
