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
	public List<ToolCallback> toolCallbacks(GraphQLToolRegistry graphQLRegistry) {
		List<ToolCallback> tools = new ArrayList<>();

		// GraphQL tools - Assuming each fieldMeta contains necessary information for
		// GraphQL queries
		graphQLRegistry.getToolMap().forEach((operationId, fieldMeta) -> {
			// Ensure GraphQLToolCallback is properly instantiated with operationId and
			// fieldMeta
			tools.add(new GraphQLToolCallback(toolExecutionService, fieldMeta.fieldDefinition()));
		});

		return tools;
	}

}
