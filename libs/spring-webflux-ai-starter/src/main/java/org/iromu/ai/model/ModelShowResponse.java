package org.iromu.ai.model;

import java.util.Map;

public record ModelShowResponse(
        String name,
        Map<String, Object> parameters,
        String details
) {
}
