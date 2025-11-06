package dev.dev_store_api.common.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.docs.swagger.openapi")
public class SwaggerProperties {
    private Server server;
    private Info info;

    @Getter
    @Setter
    public static class Server {
        private String url;
        private String description;
    }

    @Getter
    @Setter
    public static class Info {
        private String title;
        private String version;
        private String description;
        private String termsOfService;
        private Contact contact;
        private License license;
    }

    @Getter
    @Setter
    public static class Contact {
        private String name;
        private String email;
        private String url;
    }

    @Getter
    @Setter
    public static class License {
        private String name;
        private String url;
    }
}
