package io.revenium.metering.spring;

import io.revenium.metering.api.MeteringApi;
import io.revenium.metering.invoker.ApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Auto-configuration for Revenium Metering.
 */
@Configuration
@ConditionalOnClass(MeteringApi.class)
@EnableConfigurationProperties(ReveniumProperties.class)
@ComponentScan(basePackages = {"io.revenium.metering.spring"})
public class ReveniumAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ReveniumAutoConfiguration.class);

    public static final String X_API_KEY_HEADER = "x-api-key";

    private final ReveniumProperties properties;

    public ReveniumAutoConfiguration(@Autowired ReveniumProperties properties) {
        this.properties = properties;
    }

    @Bean
    @Scope("singleton")
    @ConditionalOnMissingBean
    public MeteringApi metering() {
        logger.info("Configuring Revenium Metering client with URL: {}", properties.getUrl());
        ApiClient client = new ApiClient();
        client.addDefaultHeader(X_API_KEY_HEADER, properties.getApiKey());
        client.setBasePath(properties.getUrl());
        return new MeteringApi(client);
    }
}


