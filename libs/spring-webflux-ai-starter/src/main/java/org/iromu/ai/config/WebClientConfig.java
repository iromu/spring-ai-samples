package org.iromu.ai.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

	@Bean
	public WebClientCustomizer webClientCustomizer(WebClientProperties props) {
		return builder -> {
			TcpClient tcpClient = TcpClient.create()
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) props.getConnectTimeout().toMillis())
				.doOnConnected(conn -> conn
					.addHandlerLast(new ReadTimeoutHandler(props.getReadTimeout().getSeconds(), TimeUnit.SECONDS))
					.addHandlerLast(new WriteTimeoutHandler(props.getWriteTimeout().getSeconds(), TimeUnit.SECONDS)));

			HttpClient httpClient = HttpClient.from(tcpClient).responseTimeout(props.getResponseTimeout());

			builder.clientConnector(new ReactorClientHttpConnector(httpClient));
		};
	}

}
