package org.iromu.ai.chat;

import lombok.extern.slf4j.Slf4j;
import org.iromu.ai.utils.TokenUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.vectorstore.SearchRequest;
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

	Chatbot(ChatModel chatModel, VectorStore vectorStore) {
		ChatOptions options = OllamaOptions.builder().temperature(1.0).build();
		SearchRequest searchRequest = SearchRequest.builder().build();
		chatClientBuilder = ChatClient.builder(chatModel)
			.defaultOptions(options)
			.defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore)
				.searchRequest(searchRequest)
				.promptTemplate(PromptTemplate.builder().template(SYSTEM_PROMPT_TEMPLATE).build())
				.build());
		this.chatClient = chatClientBuilder.build();

	}

	public Flux<ChatResponse> stream(String id, List<Message> messages) {

		Query query = Query.builder().text(messages.getLast().getText()).history(messages).build();

		QueryTransformer queryTransformer = CompressionQueryTransformer.builder()
			.chatClientBuilder(chatClientBuilder)
			.build();

		Mono<Query> queryMono = Mono.fromCallable(() -> {
			// Blocking call here
			return queryTransformer.transform(query);
		}).subscribeOn(Schedulers.boundedElastic());

		return queryMono.flatMapMany(transformedQuery -> {
			log.info("CompressionQueryTransformer tokens: {}", TokenUtils.countTokens(transformedQuery.text()));
			log.info("CompressionQueryTransformer: {}", transformedQuery.text());
			return chatClient.prompt(transformedQuery.text()).stream().chatResponse();
		});
	}

}
