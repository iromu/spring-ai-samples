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

@AutoConfiguration
public class SpringAiWebfluxAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    OllamaController defaultOllamaController(Optional<BuildProperties> buildProperties,
                                             @Value("${spring.application.name}") String appName,
                                             @Value("${spring.ai.ollama.chat.options.model}") String model,
                                             OllamaService ollamaService) {
        return new DefaultOllamaController(buildProperties, appName, model, ollamaService);
    }
}
