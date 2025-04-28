package org.iromu.ai.mcp;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Getter
@Slf4j
public class OpenApiToolRegistry {

	public record OperationMeta(String operationId, String baseUrl, String path, PathItem.HttpMethod method,
			Operation operation) {
	}

	private final OpenApiConfig openApiConfig;

	private final Map<String, OperationMeta> operationMap = new ConcurrentHashMap<>();

	public OpenApiToolRegistry(OpenApiConfig openApiConfig) {
		this.openApiConfig = openApiConfig;
	}

	@PostConstruct
	public void loadOpenApiFromUrls() {
		List<String> openApiUrls = openApiConfig.getUrls();

		for (String url : openApiUrls) {
			loadAndRegister(url);
		}
	}

	private void loadAndRegister(String swaggerUrl) {
		SwaggerParseResult result = new io.swagger.v3.parser.OpenAPIV3Parser().readLocation(swaggerUrl, null, null);
		OpenAPI openAPI = result.getOpenAPI();

		if (openAPI == null) {
			throw new RuntimeException("Failed to load OpenAPI from: " + swaggerUrl);
		}

		String baseUrl = extractBaseUrl(swaggerUrl);

		openAPI.getPaths().forEach((path, pathItem) -> pathItem.readOperationsMap().forEach((method, operation) -> {
			if (operation.getOperationId() != null) {
				OperationMeta meta = new OperationMeta(operation.getOperationId(), baseUrl, path, method, operation);
				operationMap.put(operation.getOperationId(), meta);
				log.info("Added {} {} {} {}", baseUrl, method, path, operation.getOperationId());
			}
		}));
	}

	private String extractBaseUrl(String swaggerUrl) {
		try {
			URI uri = new URI(swaggerUrl);
			return uri.getScheme() + "://" + uri.getHost() + ":" + uri.getPort();
		}
		catch (Exception e) {
			throw new RuntimeException("Invalid OpenAPI URL: " + swaggerUrl, e);
		}
	}

	public Optional<OperationMeta> getOperation(String operationId) {
		return Optional.ofNullable(operationMap.get(operationId));
	}

	public Collection<OperationMeta> listOperations() {
		return operationMap.values();
	}

}
