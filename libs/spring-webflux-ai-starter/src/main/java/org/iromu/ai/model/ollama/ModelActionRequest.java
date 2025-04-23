package org.iromu.ai.model.ollama;

/**
 * Represents a request to perform an action on a specific model.
 *
 * This record contains the following fields:
 *
 * - model: The identifier of the model on which the action will be performed.
 * - name: The name of the action to be executed.
 */
public record ModelActionRequest(
        String model,
        String name
) {
}
