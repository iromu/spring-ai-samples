package org.iromu.graphql;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@RestController
public class GraphQLSchemaRestController {

	private final ResourceLoader resourceLoader;

	public GraphQLSchemaRestController(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@GetMapping("/schema.graphql")
	public String getSchema() {
		try {
			Resource schemaResource = resourceLoader.getResource("classpath:graphql/schema.graphqls");
			byte[] schemaBytes = Files.readAllBytes(schemaResource.getFile().toPath());
			return new String(schemaBytes, StandardCharsets.UTF_8);
		}
		catch (Exception e) {
			return "Failed to load schema: " + e.getMessage();
		}
	}

}
