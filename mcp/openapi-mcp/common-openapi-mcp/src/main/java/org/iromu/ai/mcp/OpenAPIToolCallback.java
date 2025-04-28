package org.iromu.ai.mcp;

import io.swagger.v3.oas.models.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.util.ToolUtils;
import org.springframework.ai.util.json.JsonParser;
import org.springframework.util.StringUtils;

import java.util.Map;

@Slf4j
public class OpenAPIToolCallback implements ToolCallback {

	private final ToolExecutionService toolExecutionService;

	private final ToolDefinition toolDefinition;

	private final Operation operation;

	public OpenAPIToolCallback(ToolExecutionService toolExecutionService, String baseUrl, String path,
			Operation operation) {
		this.toolExecutionService = toolExecutionService;
		this.operation = operation;
		var name = operation.getOperationId();
		var description = operation.getDescription();
		var inputSchema = JsonSchemaGeneratorOpenAPI.generateForOperation(operation);

		String description1 = null;

		if (StringUtils.hasText(operation.getDescription())) {
			description1 = operation.getDescription();
		}
		else if (StringUtils.hasText(operation.getSummary())) {
			description1 = ToolUtils.getToolDescriptionFromName(operation.getSummary());
		}

		toolDefinition = ToolDefinition.builder().name(name).description(description1).inputSchema(inputSchema).build();
	}

	@Override
	public ToolDefinition getToolDefinition() {
		return toolDefinition;
	}

	@Override
	public String call(String toolInput) {
		log.info(toolInput);
		Map<String, Object> inputParams = JsonParser.<Map<String, Object>>fromJson(toolInput, Map.class);
		return toolExecutionService.executeTool(operation.getOperationId(), inputParams).block();
	}

	@Override
	public String call(String toolInput, ToolContext tooContext) {
		return call(toolInput);
	}
}
