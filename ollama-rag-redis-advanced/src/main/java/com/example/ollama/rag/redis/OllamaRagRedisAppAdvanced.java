package com.example.ollama.rag.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import redis.clients.jedis.params.ScanParams;

import java.util.*;

@SpringBootApplication
@Slf4j
public class OllamaRagRedisAppAdvanced {

    public static void main(String[] args) {
        SpringApplication.run(OllamaRagRedisAppAdvanced.class, args);
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> onApplicationReadyEvent(RedisVectorStore vectorStore,
                                                                       @Value("classpath:medicaid-wa-faqs.pdf") Resource resource,
                                                                       @Value("${spring.ai.vectorstore.redis.prefix}") String prefix) {
        return event -> {
            cleanupRedis(vectorStore, prefix);

            var config = PdfDocumentReaderConfig.builder()
                    .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder()
                            .build())
                    .build();

            var documentReader = new ParagraphPdfDocumentReader(resource, config);
            var textSplitter = new TokenTextSplitter();
            var documentList = textSplitter.apply(documentReader.get());
            log.info("Adding {} documents to the vector store", documentList.size());
            for (Document document : documentList) {
                log.info("Adding document {}", document.getId());
                vectorStore.accept(Collections.singletonList(document));
            }
            log.info("Added {} documents to the vector store", documentList.size());
        };
    }

    private static void cleanupRedis(RedisVectorStore vectorStore, String prefix) {
        var matchingKeys = new HashSet<String>();
        var params = new ScanParams().match(prefix + "*");
        var nextCursor = "0";
        var jedis = vectorStore.getJedis();

        do {
            var scanResult = jedis.scan(nextCursor, params);
            matchingKeys.addAll(scanResult.getResult());
            nextCursor = scanResult.getCursor();
        } while (!nextCursor.equals("0"));

        if (!matchingKeys.isEmpty()) {
            String[] array = matchingKeys.toArray((new String[0]));
            log.info("Deleting keys: {}", array);
            jedis.del(array);
        }
    }

    @Bean
    ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}

@Component
@Slf4j
class Chatbot {

    private static final String SYSTEM_PROMPT_TEMPLATE = """
            When asked who you are, answer that you're assisting with questions about services offered by Carina.
            Carina is a two-sided healthcare marketplace focusing on home care aides (caregivers)
            and their Medicaid in-home care clients (adults and children with developmental disabilities and low income elderly population).
            Carina's mission is to build online tools to bring good jobs to care workers, so care workers can provide the
            best possible care for those who need it.
            
            Use the information from the context to provide accurate answers but act as if you knew this information innately.
            If unsure, simply state that you don't know. Refuse to answer any question not provided in the context.
            Do not offer to answer questions that are not in the context.
            Do not use your training data to provide answers.
            If the question does not relate to the context, simply state that you don't know.
            
            """;
    private final ChatClient chatClient;
    private final SystemMessage systemMessage;
    private final ChatClient.Builder chatClientBuilder;
    private final Map<String, List<Message>> history = new HashMap<>();

    Chatbot(ChatModel chatModel, VectorStore vectorStore) {
        ChatOptions options = OllamaOptions.builder().temperature(0.1).build();
        chatClientBuilder = ChatClient.builder(chatModel).defaultOptions(options)
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore));
        this.chatClient = chatClientBuilder.build();

        systemMessage = new SystemMessage(SYSTEM_PROMPT_TEMPLATE);
    }

    public Flux<ChatResponse> stream(String id, String message) {

        UserMessage userMessage = new UserMessage(message);

        if (!history.containsKey(id)) {
            log.info("new history for {}", id);
            ArrayList<Message> value = new ArrayList<>();
            value.add(userMessage);
            history.put(id, value);
            final String[] response = {""};
            return chatClient.prompt(new Prompt(Arrays.asList(systemMessage, userMessage))).stream().chatResponse()
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
