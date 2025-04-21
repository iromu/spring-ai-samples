package org.iromu.ai;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ToolConfig {

    private final ToolExecutionService toolExecutionService;

    public ToolConfig(ToolExecutionService toolExecutionService) {
        this.toolExecutionService = toolExecutionService;
    }

    @Bean
    public ToolCallbackProvider tools(OpenApiToolRegistry registry) {

        List<ToolCallback> tools = new ArrayList<>();

        registry.getOperationMap().forEach((s, operationMeta) -> tools.
                add(new OpenAPIToolCallback(toolExecutionService, operationMeta.getBaseUrl(), operationMeta.getPath(), operationMeta.getOperation())));

        return ToolCallbackProvider.from(tools);
    }

}
