package com.example.ollama.rag.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
@Slf4j
public class OllamaRagRedisApp {

    public static void main(String[] args) {
        SpringApplication.run(OllamaRagRedisApp.class, args);
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> onApplicationReadyEvent(RedisVectorStore vectorStore,
                                                                       @Value("classpath:medicaid-wa-faqs.pdf") Resource resource,
                                                                       @Value("${spring.ai.vectorstore.redis.prefix}") String prefix) {
        return event -> {

            //if (vectorStore instanceof RedisVectorStore r)
            cleanupRedis(vectorStore, prefix);

            var config = PdfDocumentReaderConfig.builder()
                    .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder()
                            .build())
                    .build();

            var documentReader = new ParagraphPdfDocumentReader(resource, config);
            var textSplitter = new TokenTextSplitter();
            var documentList = textSplitter.apply(documentReader.get());
            log.info("Adding {} documents to the vector store", documentList.size());
            vectorStore.accept(documentList);
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
            
            You're assisting with questions about services offered by Carina.
            Carina is a two-sided healthcare marketplace focusing on home care aides (caregivers)
            and their Medicaid in-home care clients (adults and children with developmental disabilities and low income elderly population).
            Carina's mission is to build online tools to bring good jobs to care workers, so care workers can provide the
            best possible care for those who need it.
            
            Use the information from the DOCUMENTS section to provide accurate answers but act as if you knew this information innately.
            If unsure, simply state that you don't know.
            
            DOCUMENTS:
            {documents}
            
            """;
    private final ChatModel chatModel;
    private final VectorStore vectorStore;

    Chatbot(ChatModel chatModel, VectorStore vectorStore) {
        this.chatModel = chatModel;
        this.vectorStore = vectorStore;
    }

    public Flux<ChatResponse> stream(String message) {

        Mono<List<Document>> listMono = Mono.fromCallable(() -> {
            // Blocking call here
            return vectorStore.similaritySearch(message);
        }).subscribeOn(Schedulers.boundedElastic());

        return listMono.flatMapMany(listOfSimilarDocuments -> {
            log.info("Retrieved {} documents from vectorstore", listOfSimilarDocuments.size());
            var documents = listOfSimilarDocuments
                    .stream()
                    .map(Document::getText)
                    .collect(Collectors.joining(System.lineSeparator()));
            log.info("The documents context has {} chars", documents.length());
            var systemMessage = new SystemPromptTemplate(SYSTEM_PROMPT_TEMPLATE)
                    .createMessage(Map.of("documents", documents));
            log.info("The System prompt has {} chars", systemMessage.getText().length());
            var userMessage = new UserMessage(message);
            log.info("The User prompt has {} chars", userMessage.getText().length());
            var prompt = new Prompt(List.of(systemMessage, userMessage));
            return chatModel.stream(prompt)
                    .doOnNext(chatResponse -> log.info("{}", chatResponse.getResult().getOutput().getText()));
        });
    }
}
