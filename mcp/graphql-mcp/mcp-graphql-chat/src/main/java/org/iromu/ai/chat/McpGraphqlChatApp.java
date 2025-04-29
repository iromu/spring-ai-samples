package org.iromu.ai.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.customizer.McpAsyncClientCustomizer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class McpGraphqlChatApp {

	public static void main(String[] args) {
		SpringApplication.run(McpGraphqlChatApp.class, args);
	}

	@Bean
	public McpAsyncClientCustomizer mcpAsyncClientCustomizer() {
		return (name, spec) -> {
			spec.loggingConsumer(logingMessage -> {
				log.info(logingMessage.data());
				return null;
			});
			log.info("Customizing {}", name);
		};
	}

}
