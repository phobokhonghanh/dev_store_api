package dev.dev_store_api.libs.constant.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.jwt")
public record JwtProperties(
        long expiration,
        long refreshExpiration,
        String secretKey
) {}