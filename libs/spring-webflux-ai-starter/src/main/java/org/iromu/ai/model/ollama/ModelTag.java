package org.iromu.ai.model.ollama;

/**
 * Represents a tag associated with a model.
 *
 * This record contains the following fields:
 *
 * - name: The name of the model's tag. - size: The size attribute of the model's tag.
 *
 * The `ModelTag` is used within systems managing models and their metadata.
 */
public record ModelTag(String name, String size) {
	public String getModel() {
		return name;
	}
}
