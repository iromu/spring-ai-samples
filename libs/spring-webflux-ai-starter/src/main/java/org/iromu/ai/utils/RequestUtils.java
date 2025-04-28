package org.iromu.ai.utils;

import lombok.experimental.UtilityClass;
import org.iromu.ai.model.ChatRequest;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Utility class for transforming incoming {@code ChatRequest} data into a format suitable
 * for further processing. This class provides methods to handle the reformatting and
 * categorization of {@code Message} objects contained in a {@code ChatRequest} for
 * downstream consumption.
 */
@UtilityClass
public class RequestUtils {

	/**
	 * Transforms a list of {@code Message} objects within a {@code ChatRequest} into a
	 * list of domain-specific message objects categorized as {@code AssistantMessage} or
	 * {@code UserMessage}. The categorization is based on the {@code role} field of each
	 * {@code Message}.
	 * @param request the {@code ChatRequest} containing the list of {@code Message}
	 * objects to be transformed
	 * @return a list of transformed {@code Message} objects, where each object is an
	 * instance of either {@code AssistantMessage} or {@code UserMessage}, depending on
	 * the role specified
	 */
	public static List<Message> getMessages(ChatRequest request) {
		return request.messages().stream().map(message -> {
			switch (message.role()) {
				case "assistant":
					return new AssistantMessage(message.content());
				case "user":
				default:
					return (Message) new UserMessage(message.content());
			}
		}).collect(Collectors.toList());
	}

	public static Optional<ChatOptions> getOptions(ChatRequest request) {
		if (request.options() == null || request.options().isEmpty())
			return Optional.empty();
		ChatOptions.Builder builder = ChatOptions.builder();

		// Map the values from the options map to the builder
		Map<String, Object> options = request.options();
		if (options.containsKey("model")) {
			builder.model((String) options.get("model"));
		}
		if (options.containsKey("frequencyPenalty")) {
			builder.frequencyPenalty((Double) options.get("frequencyPenalty"));
		}
		if (options.containsKey("maxTokens")) {
			builder.maxTokens((Integer) options.get("maxTokens"));
		}
		if (options.containsKey("presencePenalty")) {
			builder.presencePenalty((Double) options.get("presencePenalty"));
		}
		if (options.containsKey("stopSequences")) {
			builder.stopSequences((List<String>) options.get("stopSequences"));
		}
		if (options.containsKey("temperature")) {
			builder.temperature((Double) options.get("temperature"));
		}
		if (options.containsKey("topK")) {
			builder.topK((Integer) options.get("topK"));
		}
		if (options.containsKey("topP")) {
			builder.topP((Double) options.get("topP"));
		}

		// Build and return the ChatOptions instance
		return Optional.of(builder.build());
	}

}
