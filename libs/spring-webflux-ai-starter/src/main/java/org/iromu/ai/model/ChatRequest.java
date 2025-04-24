package org.iromu.ai.model;

import java.util.List;
import java.util.Map;

/**
 * Represents a request to a chat-based model interaction.
 * This record encapsulates the necessary data to process
 * and generate chat-based responses from a specific AI model.
 *
 * Fields:
 * - model: The identifier of the model to be used for generating responses.
 * - messages: A list of messages exchanged in the chat, typically including roles
 *             (e.g., 'user', 'assistant') and their corresponding content.
 * - stream: A boolean indicating if the response should be streamed as multiple parts.
 * - system: A string providing the system-level context or instructions for the chat interaction.
 * - options: A map containing additional optional parameters for configuring the interaction.
 */
public record ChatRequest(
        String model,
        List<Message> messages,
        boolean stream,
        String system,
        Map<String, Object> options
) {
    public record Message(String role, String content) {
    }

    /**
     * Count the total tokens in all the messages.
     *
     * @return the total token count
     */
    public int countTotalTokens() {
        int totalTokens = 0;

        // Iterate through all messages and count tokens
        for (Message message : messages) {
            totalTokens += countTokens(message.content);
        }

        return totalTokens;
    }

    /**
     * Count tokens in the given text by splitting it into words based on whitespace.
     *
     * @param text the message content
     * @return the token count
     */
    private int countTokens(String text) {
        // Simple token count by splitting the content based on whitespace
        if (text == null || text.isEmpty()) {
            return 0;
        }
        // Split by whitespace and count the number of tokens
        return text.trim().split("\\s+").length;
    }
}
