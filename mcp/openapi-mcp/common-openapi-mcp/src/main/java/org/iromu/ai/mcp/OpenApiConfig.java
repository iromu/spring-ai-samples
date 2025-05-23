package org.iromu.ai.mcp;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "openapi")
@Data
public class OpenApiConfig {

	private List<String> urls;

}
