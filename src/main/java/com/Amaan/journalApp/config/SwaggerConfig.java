package com.Amaan.journalApp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Journal App API")
                        .version("1.0")
                        .description("By Amaan")
                        .description("API Documentation for Journal App")
                )
                .servers(List.of(new Server().url("http://localhost:8080/journalApp").description("Local"),
                                    new Server().url("http://localhost:8081").description("Live")))
                // Define tags explicitly
                .tags(List.of(
                        new Tag().name("Public APIs").description("Endpoints accessible without authentication"),
                        new Tag().name("User APIs").description("Endpoints for authenticated users"),
                        new Tag().name("Admin APIs").description("Endpoints for admin users")
                ))
                // Add x-tagGroups to force order
                .extensions(Map.of(
                        "x-tags", List.of("Public APIs", "User APIs", "Admin APIs")
                ))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components().addSecuritySchemes(
                        "bearerAuth",new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                ));

    }
}

