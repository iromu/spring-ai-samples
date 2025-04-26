package org.iromu.ai.chat;

import lombok.extern.slf4j.Slf4j;
import org.iromu.ai.utils.TokenUtils;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
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
            ### Task:
                   Respond to the user query using the provided context, incorporating inline citations in the format [id] **only when the <source> tag includes an explicit id attribute** (e.g., <source id="1">).
            
                   ### Guidelines:
                   - If you don't know the answer, clearly state that.
                   - If uncertain, ask the user for clarification.
                   - Respond in the same language as the user's query.
                   - If the context is unreadable or of poor quality, inform the user and provide the best possible answer.
                   - If the answer isn't present in the context but you possess the knowledge, explain this to the user and provide the answer using your own understanding.
                   - **Only include inline citations using [id] (e.g., [1], [2]) when the <source> tag includes an id attribute.**
                   - Do not cite if the <source> tag does not contain an id attribute.
                   - Do not use XML tags in your response.
                   - Ensure citations are concise and directly related to the information provided.
            
                   ### Example of Citation:
                   If the user asks about a specific topic and the information is found in a source with a provided id attribute, the response should include the citation like in the following example:
                   * "According to the study, the proposed method increases efficiency by 20% [1]."
            
                   ### Output:
                   Provide a clear and direct response to the user's query, including inline citations in the format [id] only when the <source> tag with id attribute is present in the context.
            
                   <context>
                   {question_answer_context}
                   </context>
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
                    .map(document -> document.getFormattedContent(MetadataMode.INFERENCE))
                    .collect(Collectors.joining(System.lineSeparator()));

            log.info("Context tokens: {}", TokenUtils.countTokens(documents));

            var systemMessage = new SystemPromptTemplate(SYSTEM_PROMPT_TEMPLATE)
                    .createMessage(Map.of("question_answer_context", documents));

            log.info("System prompt tokens: {}", TokenUtils.countTokens(systemMessage.getText()));


            List<Message> messageList = new ArrayList<>();
            messageList.add(systemMessage);
            messageList.addAll(messages);

            var prompt = new Prompt(messageList);
            return chatModel.stream(prompt);
        });
    }
}
