package dev.dev_store_api.common.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(String domain, String version, Api api ) {
    public record Api(String context, String url) {}
}
