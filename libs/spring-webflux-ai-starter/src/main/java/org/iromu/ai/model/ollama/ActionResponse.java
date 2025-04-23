package org.iromu.ai.model.ollama;

public record ActionResponse(
        String status,
        String digest,
        int total,
        int completed,
        boolean done
) {
}
