package io.revenium.metering.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

/*
  Configuration properties for Revenium Metering.
 */
@ConfigurationProperties(prefix = "revenium.metering")
public class ReveniumProperties {

    /**
     * The API key to use for the Revenium Metering API
     */
    private String apiKey;

    /**
     * The URL of the Revenium Metering API
     */
    private String url = "https://api.revenium.io/meter/v1/api";

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
