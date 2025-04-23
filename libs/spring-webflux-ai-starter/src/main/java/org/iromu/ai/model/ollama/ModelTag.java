package org.iromu.ai.model.ollama;

public record ModelTag(
        String name,
        String size
) {
    public String getModel() {
        return name;
    }
}
