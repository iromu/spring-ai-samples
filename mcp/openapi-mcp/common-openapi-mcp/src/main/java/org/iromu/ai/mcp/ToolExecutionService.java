package org.iromu.ai.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ToolExecutionService {

	private final WebClient.Builder webClientBuilder;

	private final OpenApiToolRegistry registry;

	public Mono<String> executeTool(String operationId, Map<String, Object> args) {
		var metaOpt = registry.getOperation(operationId);
		if (metaOpt.isEmpty()) {
			return Mono.error(new IllegalArgumentException("Unknown tool: " + operationId));
		}

		var meta = metaOpt.get();
		HttpMethod method = HttpMethod.valueOf(meta.method().name());

		String resolvedPath = resolvePath(meta.path(), args);
		URI fullUri = buildUriWithQueryParams(meta.baseUrl() + resolvedPath, args, meta.operation());

		WebClient webClient = webClientBuilder.baseUrl(meta.baseUrl()).build();
		WebClient.RequestBodySpec request = webClient.method(method).uri(fullUri);

		if (meta.operation().getRequestBody() != null) {
			ObjectNode body = buildJsonBody(meta.operation().getRequestBody(), args);
			return request.bodyValue(body).retrieve().bodyToMono(String.class);
		}
		else {
			return request.retrieve().bodyToMono(String.class);
		}
	}

	private String resolvePath(String path, Map<String, Object> args) {
		Pattern pattern = Pattern.compile("\\{(\\w+)}");
		Matcher matcher = pattern.matcher(path);
		StringBuilder result = new StringBuilder();

		while (matcher.find()) {
			String key = matcher.group(1);
			Object value = args.getOrDefault(key, "");
			matcher.appendReplacement(result, Matcher.quoteReplacement(value.toString()));
		}

		matcher.appendTail(result);
		return result.toString();
	}

	private URI buildUriWithQueryParams(String basePath, Map<String, Object> args, Operation operation) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(basePath);
		List<Parameter> parameters = operation.getParameters();
		if (parameters != null) {
			for (Parameter param : parameters) {
				if ("query".equals(param.getIn()) && args.containsKey(param.getName())) {
					builder.queryParam(param.getName(), args.get(param.getName()));
				}
			}
		}
		return builder.build().toUri();
	}

	private ObjectNode buildJsonBody(RequestBody requestBody, Map<String, Object> args) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode json = mapper.createObjectNode();

		if (requestBody.getContent().containsKey("application/json")) {
			requestBody.getContent().get("application/json").getSchema().getProperties().forEach((key, value) -> {
				if (args.containsKey(key)) {
					json.putPOJO((String) key, args.get(key));
				}
			});
		}
		return json;
	}

}
