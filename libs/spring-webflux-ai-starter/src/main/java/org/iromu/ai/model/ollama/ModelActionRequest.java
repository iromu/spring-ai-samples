package org.iromu.ai.model.ollama;

public record ModelActionRequest(
        String model,
        String name
) {
}
