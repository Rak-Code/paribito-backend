package com.ecommerce.project.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger Configuration for API Documentation
 * Configures Swagger UI with development and production server URLs
 */
@Configuration
public class OpenApiConfig {

    @Value("${springdoc.swagger-ui.path:/swagger-ui.html}")
    private String swaggerUiPath;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(
                        List.of(
                                // Development Server
                                new Server()
                                        .url("http://localhost:8080")
                                        .description("Development Server"),
                                // Production Server
                                new Server()
                                        .url("https://paribito-backend.onrender.com")
                                        .description("Production Server")
                        )
                )
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT Bearer token from login endpoint. Format: Bearer <token>")
                        )
                );
    }

    private Info apiInfo() {
        return new Info()
                .title("Paribito E-Commerce API")
                .description("Paribito E-Commerce REST API")
                .version("1.0.0")
                .contact(apiContact())
                .license(apiLicense());
    }

    private Contact apiContact() {
        return new Contact()
                .name("Paribito Support")
                .email("aditaenterpriseindia@gmail.com")
                .url("https://theparibito.com");
    }

    private License apiLicense() {
        return new License()
                .name("Proprietary License")
                .url("https://theparibito.com/license");
    }
}
