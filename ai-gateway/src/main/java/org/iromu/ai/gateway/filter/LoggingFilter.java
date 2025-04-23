package org.iromu.ai.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * A logging filter that implements the {@link WebFilter} interface to log the details of incoming HTTP requests.
 *
 * This filter logs information such as the HTTP method, the request URI, and the remote address of the client.
 * The logging is performed using SLF4J's {@link Logger}.
 *
 * The filter is a Spring-managed component and will be applied as part of the reactive web filter chain.
 * It ensures that the requests are logged before proceeding to the next filter or handler in the chain.
 *
 * Key details logged:
 * - HTTP Method
 * - Request URI
 * - Remote Address
 *
 * Implements {@link WebFilter} to enforce reactive request processing.
 */
@Component
public class LoggingFilter implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Log request details
        String method = exchange.getRequest().getMethod().name();
        String uri = exchange.getRequest().getURI().toString();
        String remoteAddress = exchange.getRequest().getRemoteAddress() != null
                ? exchange.getRequest().getRemoteAddress().getHostString()
                : "unknown";

        logger.info("Request Info: {} {} | Method: {} | Remote Address: {}",
                uri, method, method, remoteAddress);

        // Proceed with the next filter or handler in the chain
        return chain.filter(exchange);
    }
}
