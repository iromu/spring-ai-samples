package org.iromu.ai.model;

import java.util.List;

public record ChatMessage(
        String role,
        String content,
        List<String> images
) {
}
