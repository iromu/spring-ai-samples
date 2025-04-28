package org.iromu.ai.mcp;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ConfigMCP {

	private final List<String> services;

	public ConfigMCP(OpenApiConfig openApiConfig) {
		this.services = openApiConfig.getUrls();
	}

	@Bean
	public ToolCallbackProvider tools() {
		List<ToolCallback> tools = new ArrayList<>();
		services.forEach(service -> {
			var service_tools = buildToolsFromOpenAPI(service);
			tools.addAll(service_tools);
		});

		return ToolCallbackProvider.from(tools);
	}

	private List<ToolCallback> buildToolsFromOpenAPI(String url) {
		List<ToolCallback> tools = new ArrayList<>();

		SwaggerParseResult result = new OpenAPIParser().readLocation(url, null, null);
		OpenAPI openAPI = result.getOpenAPI();
		if (openAPI != null) {
			var baseUrl = openAPI.getServers().getFirst().getUrl();
			openAPI.getPaths().forEach((path, item) -> {
				tools.add(new OpenAPIToolCallback(baseUrl, path, item.getGet()));
			});
		}

		return tools;
	}

}
