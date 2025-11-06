package dev.dev_store_api.common.config.docs;

import dev.dev_store_api.common.config.properties.SwaggerProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class OpenAPIConfig {

    private final SwaggerProperties swaggerProperties;

    @Bean
    public OpenAPI myOpenAPI() {
        Server server = new Server()
                .url(swaggerProperties.getServer().getUrl())
                .description(swaggerProperties.getServer().getDescription());

        Contact contact = new Contact()
                .name(swaggerProperties.getInfo().getContact().getName())
                .email(swaggerProperties.getInfo().getContact().getEmail())
                .url(swaggerProperties.getInfo().getContact().getUrl());

        License license = new License()
                .name(swaggerProperties.getInfo().getLicense().getName())
                .url(swaggerProperties.getInfo().getLicense().getUrl());

        Info info = new Info()
                .title(swaggerProperties.getInfo().getTitle())
                .version(swaggerProperties.getInfo().getVersion())
                .description(swaggerProperties.getInfo().getDescription())
                .termsOfService(swaggerProperties.getInfo().getTermsOfService())
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }

}
