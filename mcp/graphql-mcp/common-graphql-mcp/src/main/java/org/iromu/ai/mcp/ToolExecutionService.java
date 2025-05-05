package org.iromu.ai.mcp;

import graphql.schema.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ToolExecutionService {

	private final WebClient.Builder webClientBuilder;

	private final GraphQLToolRegistry graphQLRegistry;

	public ToolExecutionService(WebClient.Builder webClientBuilder, GraphQLToolRegistry graphQLRegistry) {
		this.webClientBuilder = webClientBuilder;
		this.graphQLRegistry = graphQLRegistry;
	}

	public Mono<String> executeTool(String operationId, Map<String, Object> args) {
		var fieldMeta = graphQLRegistry.getTool(operationId);
		if (fieldMeta == null || fieldMeta.isEmpty()) {
			return Mono.error(new IllegalArgumentException("Unknown tool: " + operationId));
		}

		return executeGraphQLTool(fieldMeta, args);
	}

	private Mono<String> executeGraphQLTool(GraphQLToolRegistry.FieldMeta meta, Map<String, Object> args) {
		WebClient webClient = webClientBuilder.baseUrl(meta.operationUrl()).build();
		String query = buildGraphQLQuery(meta.fieldDefinition(), args);

		log.info("Executing GraphQL raw query to {}:\n{}", meta.operationUrl(), query);

		return webClient.post()
			.header("Content-Type", "application/graphql")
			.bodyValue(query)
			.retrieve()
			.bodyToMono(String.class);
	}

	private String buildGraphQLQuery(GraphQLFieldDefinition fieldDef, Map<String, Object> args) {
		String argString = buildGraphQLArgs(args);
		String selectionSet = buildSelectionSet(fieldDef.getType());
		return "{\"query\":\"{ " + fieldDef.getName()
				+ (argString.isEmpty() ? "" : "(" + argString.replaceAll("\"", "\\\\\"") + ")")
				+ (selectionSet.isEmpty() ? "" : " " + selectionSet) + " }\"}";
	}

	private String buildGraphQLArgs(Map<String, Object> args) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Object> entry : args.entrySet()) {
			sb.append(entry.getKey()).append(": ").append(formatGraphQLArgValue(entry.getValue())).append(", ");
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - 2);
		}
		return sb.toString();
	}

	private String formatGraphQLArgValue(Object value) {
		if (value instanceof String) {
			return "\"" + escapeString((String) value) + "\"";
		}
		else if (value instanceof Number || value instanceof Boolean) {
			return value.toString();
		}
		else {
			return "\"" + value.toString() + "\"";
		}
	}

	private String escapeString(String input) {
		return input.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
	}

	private String buildSelectionSet(GraphQLOutputType type) {
		type = unwrapType(type);
		if (type instanceof GraphQLObjectType objectType) {
			List<GraphQLFieldDefinition> fields = objectType.getFieldDefinitions();
			if (!fields.isEmpty()) {
				StringBuilder sb = new StringBuilder("{ ");
				for (GraphQLFieldDefinition field : fields) {
					sb.append(field.getName());
					String nestedSelection = buildSelectionSet(field.getType());
					if (!nestedSelection.isEmpty()) {
						sb.append(" ").append(nestedSelection);
					}
					sb.append(" ");
				}
				sb.append("}");
				return sb.toString();
			}
		}
		return "";
	}

	private GraphQLOutputType unwrapType(GraphQLOutputType type) {
		while (type instanceof GraphQLNonNull || type instanceof GraphQLList) {
			if (type instanceof GraphQLNonNull nonNull) {
				type = (GraphQLOutputType) nonNull.getWrappedType();
			}
			else if (type instanceof GraphQLList list) {
				type = (GraphQLOutputType) list.getWrappedType();
			}
		}
		return type;
	}

}
