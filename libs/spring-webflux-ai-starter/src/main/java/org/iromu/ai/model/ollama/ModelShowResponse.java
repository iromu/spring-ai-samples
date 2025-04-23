package org.iromu.ai.model.ollama;

import java.util.Map;

/**
 * Represents the response containing details about a specific model.
 *
 * This record encapsulates the following information:
 *
 * - name: The name of the model being described.
 * - parameters: A map of key-value pairs representing the parameters associated with the model.
 * - details: Additional details or metadata about the model.
 */
public record ModelShowResponse(
        String name,
        Map<String, Object> parameters,
        String details
) {
}
