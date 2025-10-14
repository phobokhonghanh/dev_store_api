package dev.dev_store_api.config.docs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class OpenAPIConfig {

    private final SwaggerProperties swaggerProperties;

    @Bean
    public OpenAPI myOpenAPI() {
        // 🧭 Server info
        Server server = new Server()
                .url(swaggerProperties.getServer().getUrl())
                .description(swaggerProperties.getServer().getDescription());

        // 👤 Contact info
        Contact contact = new Contact()
                .name(swaggerProperties.getInfo().getContact().getName())
                .email(swaggerProperties.getInfo().getContact().getEmail())
                .url(swaggerProperties.getInfo().getContact().getUrl());

        // 📜 License info
        License license = new License()
                .name(swaggerProperties.getInfo().getLicense().getName())
                .url(swaggerProperties.getInfo().getLicense().getUrl());

        // 📦 API meta info
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
