package org.iromu.ai.model.ollama;

import java.util.List;

/**
 * Represents a chat message exchanged in a conversation.
 *
 * This record contains the following components:
 *
 * - role: Identifies the role of the speaker in the conversation, such as "user" or
 * "assistant". - content: The textual content of the message. - images: A list of image
 * references associated with the message, if any.
 */
public record ChatMessage(String role, String content, List<String> images) {
}
