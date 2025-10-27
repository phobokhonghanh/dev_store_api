package dev.dev_store_api.libs.constant.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.api")
public record ApiProperties(String domain, String version, String url) {
}
