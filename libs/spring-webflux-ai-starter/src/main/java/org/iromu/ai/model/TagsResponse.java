package org.iromu.ai.model;

import java.util.List;

public record TagsResponse(
        List<ModelTag> models
) {
}
