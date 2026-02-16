package br.com.lucascosta.helpdeskbff.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    private static final String JWT = "JWT";
    private static final String BEARER = "bearer";
    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI(
            @Value("${springdoc.openapi.info.title}") final String title,
            @Value("${springdoc.openapi.info.description}") final String description,
            @Value("${springdoc.openapi.info.version}") final String version
    ) {
        return new OpenAPI()
                .info(new Info().title(title).description(description).version(version))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .components(
                        new Components()
                                .addSecuritySchemes(BEARER_AUTH,
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme(BEARER)
                                                .bearerFormat(JWT)
                                ));
    }
}
