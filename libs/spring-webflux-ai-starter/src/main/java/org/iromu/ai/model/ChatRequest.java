package org.iromu.ai.model;

import java.util.List;
import java.util.Map;

public record ChatRequest(
        String model,
        List<Message> messages,
        boolean stream,
        String system,
        Map<String, Object> options
) {
    public record Message(
            String role, String content
    ) {
    }
}
