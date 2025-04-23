package org.iromu.ai.model.ollama;

import java.util.List;

public record TagsResponse(
        List<ModelTag> models
) {
}
