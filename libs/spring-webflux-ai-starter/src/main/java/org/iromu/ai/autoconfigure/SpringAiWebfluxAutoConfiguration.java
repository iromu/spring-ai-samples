package org.iromu.ai.autoconfigure;

import org.iromu.ai.controller.DefaultOllamaController;
import org.iromu.ai.controller.OllamaController;
import org.iromu.ai.service.OllamaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

/**
 * SpringAiWebfluxAutoConfiguration is an auto-configuration class for setting up
 * default configurations and beans related to chat-based services in a Spring
 * WebFlux environment. This configuration class ensures that required beans
 * are registered only if they are not already present in the application context.
 *
 * Primary Responsibilities:
 * - Auto-configures the {@code OllamaController} bean if it is missing in the context.
 * - Provides a default implementation of {@code OllamaController} using the
 *   {@code DefaultOllamaController} class.
 *
 * Dependency Injection:
 * - Injects optional build properties to display application build information.
 * - Injects application name and model configuration properties.
 * - Requires an implementation of {@code OllamaService} to handle the business
 *   logic around chat services.
 *
 * Annotations:
 * - {@code @AutoConfiguration}: Indicates that this is a Spring Boot
 *   auto-configuration class.
 * - {@code @Bean}: Declares a method for defining and registering a bean
 *   in the application context.
 * - {@code @ConditionalOnMissingBean}: Ensures that the bean is only created
 *   if no matching bean of the same type exists in the context.
 */
@AutoConfiguration
public class SpringAiWebfluxAutoConfiguration {

    /**
     * Provides a default implementation of the {@code OllamaController} bean if no other
     * implementation is present in the application context. This method uses the
     * {@code DefaultOllamaController} class to wire dependencies and configure default behavior.
     *
     * @param buildProperties optional build properties providing metadata about the application's build.
     * @param appName the name of the application, typically retrieved from the {@code spring.application.name} property.
     * @param model the default model to be used for chat interactions, retrieved from the {@code spring.ai.ollama.chat.options.model} property.
     * @param ollamaService the service implementation for handling chat-based operations, responsible for processing and streaming responses.
     * @return an instance of {@code DefaultOllamaController} configured with the provided dependencies.
     */
    @Bean
    @ConditionalOnMissingBean
    OllamaController defaultOllamaController(Optional<BuildProperties> buildProperties,
                                             @Value("${spring.application.name}") String appName,
                                             @Value("${spring.ai.ollama.chat.options.model}") String model,
                                             OllamaService ollamaService) {
        return new DefaultOllamaController(buildProperties, appName, model, ollamaService);
    }
}
