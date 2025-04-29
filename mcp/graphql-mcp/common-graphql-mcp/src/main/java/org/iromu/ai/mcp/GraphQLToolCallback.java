package org.iromu.ai.mcp;

import graphql.schema.GraphQLFieldDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.util.ToolUtils;
import org.springframework.ai.util.json.JsonParser;
import org.springframework.util.StringUtils;

import java.util.Map;

@Slf4j
public class GraphQLToolCallback implements ToolCallback {

	private final ToolExecutionService toolExecutionService;

	private final ToolDefinition toolDefinition;

	private final GraphQLFieldDefinition fieldDefinition;

	public GraphQLToolCallback(ToolExecutionService toolExecutionService, GraphQLFieldDefinition fieldDefinition) {
		this.toolExecutionService = toolExecutionService;
		this.fieldDefinition = fieldDefinition;

		String name = fieldDefinition.getName();
		String inputSchema = JsonSchemaGeneratorGraphQL.generateForField(fieldDefinition);

		String description = null;
		if (StringUtils.hasText(fieldDefinition.getDescription())) {
			description = fieldDefinition.getDescription();
		}
		else {
			description = ToolUtils.getToolDescriptionFromName(name);
		}

		this.toolDefinition = ToolDefinition.builder()
			.name(name)
			.description(description)
			.inputSchema(inputSchema)
			.build();
	}

	@Override
	public ToolDefinition getToolDefinition() {
		return toolDefinition;
	}

	@Override
	public String call(String toolInput) {
		log.info("Calling GraphQL Tool '{}': input={}", fieldDefinition.getName(), toolInput);
		Map<String, Object> inputParams = JsonParser.<Map<String, Object>>fromJson(toolInput, Map.class);
		return toolExecutionService.executeTool(fieldDefinition.getName(), inputParams).block();
	}

	@Override
	public String call(String toolInput, ToolContext toolContext) {
		return call(toolInput);
	}

}
