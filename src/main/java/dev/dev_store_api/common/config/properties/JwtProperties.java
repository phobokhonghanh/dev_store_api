package dev.dev_store_api.common.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.jwt")
public record JwtProperties(
        long expiration,
        long refreshExpiration,
        String secretKey
) {}