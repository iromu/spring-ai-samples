package org.iromu.ai.model.openai;

public record ChatStreamResponse(
        String id,
        String object,
        long created,
        String model,
        Choice[] choices
) {
    public record Choice(
            Delta delta,
            int index,
            String finish_reason
    ) {
    }

    public record Delta(
            String role,
            String content
    ) {
    }
}
