package org.iromu.ai.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class SpringAIGatewayApplication {

    @SuppressWarnings("squid:S4823")
    public static void main(String... args) {
        SpringApplication.run(SpringAIGatewayApplication.class, args);
    }

}
