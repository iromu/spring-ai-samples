package org.iromu.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "webclient")
@Data
public class WebClientProperties {

	private Duration connectTimeout = Duration.ofSeconds(10);

	private Duration readTimeout = Duration.ofSeconds(10);

	private Duration writeTimeout = Duration.ofSeconds(10);

	private Duration responseTimeout = Duration.ofSeconds(10);

}
