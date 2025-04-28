package org.iromu.ai.model.ollama;

import java.util.List;

/**
 * Represents a response containing a list of model tags.
 *
 * This record encapsulates a collection of tags, where each tag provides metadata
 * associated with a specific model. It provides a structured way to retrieve and manage
 * the tags for models in the system.
 *
 * Fields:
 *
 * - models: A list of `ModelTag` objects representing the tags associated with models.
 */
public record TagsResponse(List<ModelTag> models) {
}
