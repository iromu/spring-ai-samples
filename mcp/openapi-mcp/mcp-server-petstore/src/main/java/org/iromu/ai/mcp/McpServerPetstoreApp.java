package org.iromu.ai.mcp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class McpServerPetstoreApp {

	public static void main(String[] args) {
		SpringApplication.run(McpServerPetstoreApp.class, args);
	}

}
