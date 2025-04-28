package org.iromu.ai.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Spring AI Gateway Application.
 *
 * This class initializes the Spring application context and launches the application. It
 * is annotated with {@code @SpringBootApplication}, marking it as a Spring Boot
 * application. Additionally, it leverages the {@code @Slf4j} annotation to enable logging
 * capabilities.
 *
 * The application enables AI-related features by interacting with external services and
 * components configured through the {@link AiConfig} class. It binds properties using a
 * configuration prefix and works with components like {@link TagDiscoveryService} to
 * perform tasks such as tag discovery and handling chat operations.
 *
 * Responsibilities: - Initializes the Spring Boot application via
 * {@link SpringApplication#run}. - Serves as the starting point for the entire
 * application context.
 */
@SpringBootApplication
@Slf4j
public class SpringAIGatewayApplication {

	@SuppressWarnings("squid:S4823")
	public static void main(String... args) {
		SpringApplication.run(SpringAIGatewayApplication.class, args);
	}

}
