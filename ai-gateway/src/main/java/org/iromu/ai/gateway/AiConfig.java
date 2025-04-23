package org.iromu.ai.gateway;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Configuration class for AI settings.
 *
 * This class is used to bind properties defined in the application's configuration
 * with the prefix "ai". It provides access to configurable properties such as
 * a list of URLs that are required for enabling certain AI-related functionalities.
 *
 * These properties can be set in configuration sources like `application.properties`
 * or `application.yml`.
 *
 * Example property format for configuration:
 * ai.urls:
 *   - http://example1.com
 *   - http://example2.com
 *
 * Used by components such as {@link TagDiscoveryService}
 * to perform tasks like interacting with external services or tag discovery
 * based on the configured URLs.
 */
@Data
@Component
@ConfigurationProperties(prefix = "ai")
public class AiConfig {

    private List<String> urls;

}
