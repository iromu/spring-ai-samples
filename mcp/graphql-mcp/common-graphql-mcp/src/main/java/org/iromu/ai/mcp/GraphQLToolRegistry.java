package org.iromu.ai.mcp;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class GraphQLToolRegistry {

	public record FieldMeta(GraphQLFieldDefinition fieldDefinition, String operationUrl) {
		public boolean isEmpty() {
			return fieldDefinition == null || operationUrl == null;
		}
	}

	private final Map<String, FieldMeta> toolMap = new ConcurrentHashMap<>();

	private final GraphQLConfig graphQLConfig;

	private final ResourceLoader resourceLoader;

	public GraphQLToolRegistry(GraphQLConfig graphQLConfig, ResourceLoader resourceLoader) {
		this.graphQLConfig = graphQLConfig;
		this.resourceLoader = resourceLoader;
	}

	public FieldMeta getTool(String operationId) {
		return toolMap.get(operationId);
	}

	public void registerTool(String operationId, GraphQLFieldDefinition fieldDefinition, String operationUrl) {
		toolMap.put(operationId, new FieldMeta(fieldDefinition, operationUrl));
		log.info("Registered GraphQL tool: {} at {}", operationId, operationUrl);
	}

	public Map<String, FieldMeta> getToolMap() {
		return toolMap;
	}

	@PostConstruct
	public void loadSchemas() {
		TypeDefinitionRegistry combinedRegistry = new TypeDefinitionRegistry();

		for (String schemaLocation : graphQLConfig.getSchemas()) {
			try (InputStreamReader reader = loadSchemaReader(schemaLocation)) {
				SchemaParser parser = new SchemaParser();
				TypeDefinitionRegistry registry = parser.parse(reader);
				combinedRegistry.merge(registry);
				log.info("Loaded schema: {}", schemaLocation);
			}
			catch (Exception e) {
				log.error("Failed to load GraphQL schema from {}", schemaLocation, e);
			}
		}

		RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring().build();
		GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(combinedRegistry, wiring);

		String operationUrl = "http://localhost:11180/api"; // You can make this
															// configurable

		if (schema.getQueryType() != null) {
			for (GraphQLFieldDefinition field : schema.getQueryType().getFieldDefinitions()) {
				registerTool(field.getName(), field, operationUrl);
			}
		}

		if (schema.getMutationType() != null) {
			for (GraphQLFieldDefinition field : schema.getMutationType().getFieldDefinitions()) {
				registerTool(field.getName(), field, operationUrl);
			}
		}
	}

	private InputStreamReader loadSchemaReader(String location) throws Exception {
		if (location.startsWith("http://") || location.startsWith("https://")) {
			return new InputStreamReader(new URL(location).openStream());
		}
		else {
			Resource resource = resourceLoader.getResource(location);
			return new InputStreamReader(resource.getInputStream());
		}
	}

}
