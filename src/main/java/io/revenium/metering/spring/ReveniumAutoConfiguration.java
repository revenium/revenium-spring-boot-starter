package io.revenium.metering.spring;

import io.revenium.metering.api.MeteringApi;
import io.revenium.metering.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@EnableConfigurationProperties(ReveniumProperties.class)
public class ReveniumAutoConfiguration {
    public static final String X_API_KEY_HEADER = "x-api-key";

    private final ReveniumProperties properties;

    public ReveniumAutoConfiguration(@Autowired ReveniumProperties properties) {
        this.properties = properties;
    }

    @Bean
    @Scope("singleton")
    public MeteringApi metering() {
        ApiClient client = new ApiClient();
        client.addDefaultHeader(X_API_KEY_HEADER, properties.getApiKey());
        client.setBasePath(properties.getUrl());
        return new MeteringApi(client);
    }
}


