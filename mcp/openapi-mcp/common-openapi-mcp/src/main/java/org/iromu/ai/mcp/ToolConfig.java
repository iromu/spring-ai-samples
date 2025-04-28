package org.iromu.ai.mcp;

import org.springframework.ai.tool.ToolCallback;
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
	public List<ToolCallback> toolCallbacks(OpenApiToolRegistry registry) {

		List<ToolCallback> tools = new ArrayList<>();

		registry.getOperationMap()
			.forEach((s, operationMeta) -> tools.add(new OpenAPIToolCallback(toolExecutionService,
					operationMeta.baseUrl(), operationMeta.path(), operationMeta.operation())));

		return tools;
	}

	// @Bean
	// public ToolCallbackProvider tools(List<ToolCallback> toolCallbacks) {
	// return ToolCallbackProvider.from(toolCallbacks);
	// }

}
