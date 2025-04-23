package org.iromu.ai.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
class Chatbot {

    private static final String SYSTEM_PROMPT_TEMPLATE = """
            
            You're assisting with questions about services offered by Carina, when asked who you are.
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

    public Flux<ChatResponse> stream(List<Message> messages) {

        Mono<List<Document>> listMono = Mono.fromCallable(() -> {
            // Blocking call here
            return vectorStore.similaritySearch(messages.getLast().getText());
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


            List<Message> messageList = new ArrayList<>();
            messageList.add(systemMessage);
            messageList.addAll(messages);

            var prompt = new Prompt(messageList);
            return chatModel.stream(prompt);
        });
    }
}
