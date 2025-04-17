package org.iromu.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/tools")
@RequiredArgsConstructor
public class ToolController {

    private final ToolExecutionService toolExecutionService;

    @PostMapping("/{operationId}")
    public Mono<String> executeTool(@PathVariable String operationId, @RequestBody Map<String, Object> args) {
        return toolExecutionService.executeTool(operationId, args)
                .onErrorResume(e -> Mono.just("Error: " + e.getMessage()));
    }
}
