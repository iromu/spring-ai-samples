package org.iromu.ai.mcp;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ConfigTools {

	private final ToolsRegister toolsRegister;

	private final List<String> services;

	public ConfigTools(OpenApiConfig openApiConfig, ToolsRegister toolsRegister) {
		this.services = openApiConfig.getUrls();
		this.toolsRegister = toolsRegister;
	}

	@PostConstruct
	public void loadTools() {
		services.forEach(toolsRegister::register);
	}

}
