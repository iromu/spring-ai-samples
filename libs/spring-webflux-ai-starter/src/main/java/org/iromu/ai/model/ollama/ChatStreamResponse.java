package org.iromu.ai.model.ollama;

public record ChatStreamResponse(
        String model,
        String created_at,
        ChatMessage message,
        boolean done,
        Long total_duration,
        Long load_duration,
        Integer prompt_eval_count,
        Long prompt_eval_duration,
        Integer eval_count,
        Long eval_duration
) {

    public static class Builder {
        private String model;
        private String created_at;
        private ChatMessage message;
        private boolean done;
        private Long total_duration;
        private Long load_duration;
        private Integer prompt_eval_count;
        private Long prompt_eval_duration;
        private Integer eval_count;
        private Long eval_duration;

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder createdAt(String created_at) {
            this.created_at = created_at;
            return this;
        }

        public Builder message(ChatMessage message) {
            this.message = message;
            return this;
        }

        public Builder done(boolean done) {
            this.done = done;
            return this;
        }

        public Builder totalDuration(Long total_duration) {
            this.total_duration = total_duration;
            return this;
        }

        public Builder loadDuration(Long load_duration) {
            this.load_duration = load_duration;
            return this;
        }

        public Builder promptEvalCount(Integer prompt_eval_count) {
            this.prompt_eval_count = prompt_eval_count;
            return this;
        }

        public Builder promptEvalDuration(Long prompt_eval_duration) {
            this.prompt_eval_duration = prompt_eval_duration;
            return this;
        }

        public Builder evalCount(Integer eval_count) {
            this.eval_count = eval_count;
            return this;
        }

        public Builder evalDuration(Long eval_duration) {
            this.eval_duration = eval_duration;
            return this;
        }

        public ChatStreamResponse build() {
            return new ChatStreamResponse(
                    model,
                    created_at,
                    message,
                    done,
                    total_duration,
                    load_duration,
                    prompt_eval_count,
                    prompt_eval_duration,
                    eval_count,
                    eval_duration
            );
        }
    }
}
