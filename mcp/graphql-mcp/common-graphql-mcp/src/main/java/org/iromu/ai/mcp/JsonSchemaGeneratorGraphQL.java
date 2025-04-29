package org.iromu.ai.mcp;

import com.fasterxml.jackson.databind.node.ObjectNode;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLTypeUtil;
import org.springframework.ai.util.json.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class JsonSchemaGeneratorGraphQL {

	/**
	 * Generate a JSON Schema for a GraphQL field's arguments.
	 */
	public static String generateForField(GraphQLFieldDefinition fieldDefinition) {
		ObjectNode schema = JsonParser.getObjectMapper().createObjectNode();
		schema.put("$schema", "https://json-schema.org/draft/2020-12/schema");
		schema.put("type", "object");

		ObjectNode properties = schema.putObject("properties");
		List<String> required = new ArrayList<>();

		for (GraphQLArgument argument : fieldDefinition.getArguments()) {
			String argName = argument.getName();
			ObjectNode argNode = JsonParser.getObjectMapper().createObjectNode();

			String typeName = extractJsonType(argument.getType().toString());
			argNode.put("type", typeName);
			argNode.put("description", getArgumentDescription(argument));

			properties.set(argName, argNode);

			if (isRequired(argument)) {
				required.add(argName);
			}
		}

		var requiredArray = schema.putArray("required");
		required.forEach(requiredArray::add);

		return schema.toPrettyString();
	}

	private static boolean isRequired(GraphQLArgument argument) {
		return GraphQLTypeUtil.unwrapNonNull(argument.getType()) != argument.getType();
	}

	private static String extractJsonType(String graphQLType) {
		graphQLType = graphQLType.replaceAll("[!\\[\\]]", "").toLowerCase();

		switch (graphQLType) {
			case "int":
			case "float":
				return "number";
			case "boolean":
				return "boolean";
			case "string":
			default:
				return "string";
		}
	}

	private static String getArgumentDescription(GraphQLArgument argument) {
		String desc = argument.getDescription();
		if (desc == null || desc.isEmpty()) {
			desc = argument.getName();
		}

		if (argument.getArgumentDefaultValue() != null) {
			desc += " (default: " + argument.getArgumentDefaultValue() + ")";
		}

		return desc;
	}

}
