package org.iromu.ai.model.ollama;

/**
 * Represents a response for an action performed on a model or resource.
 *
 * This record encapsulates the status, a unique digest identifier, the total
 * number of operations for the action, the number of completed operations,
 * and whether the action is completed.
 *
 * Fields:
 *
 * - status: The current status of the action, represented as a text description.
 * - digest: A unique identifier for the action, used for tracking and reference.
 * - total: The total number of operations or steps involved in the action.
 * - completed: The number of operations or steps that have been completed so far.
 * - done: Indicates whether the action has been completed, with `true` for completed
 *         and `false` otherwise.
 */
public record ActionResponse(
        String status,
        String digest,
        int total,
        int completed,
        boolean done
) {
}
