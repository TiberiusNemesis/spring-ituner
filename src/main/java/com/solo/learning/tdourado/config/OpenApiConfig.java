package com.solo.learning.tdourado.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3.0 configuration for automatic API documentation. Provides Swagger UI, Redoc, and
 * OpenAPI JSON/YAML specifications.
 *
 * <p>Available endpoints:
 *
 * <ul>
 *   <li>Swagger UI: <a href="http://localhost:8080/swagger-ui.html">/swagger-ui.html</a>
 *   <li>Redoc: <a href="http://localhost:8080/redoc.html">/redoc.html</a>
 *   <li>OpenAPI JSON: <a href="http://localhost:8080/api-docs">/api-docs</a>
 * </ul>
 *
 * @author tiberiusdourado
 */
@Configuration
public class OpenApiConfig {

  /**
   * Configures OpenAPI 3.0 documentation for the iTunes API integration.
   *
   * @return OpenAPI configuration with project metadata
   */
  @Bean
  public OpenAPI iTunesOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Spring iTuner API")
                .description(
                    """
                    A modern Spring Boot 4.0 microservice providing iTunes Search API integration.

                    This service enables searching for music artists and retrieving album information
                    using the iTunes Store API. Built with Java 25, WebClient for reactive HTTP calls,
                    and comprehensive API documentation via Redoc.

                    ## Features
                    - Artist search by name with fuzzy matching
                    - Album lookup by artist ID
                    - Reactive, non-blocking HTTP calls using WebClient
                    - Comprehensive error handling and logging
                    - Spring Actuator health checks and metrics
                    """)
                .version("1.0")
                .contact(
                    new Contact()
                        .name("Tiberius Dourado")
                        .email("tiberiusdourado@example.com")
                        .url("https://github.com/tdourado/spring-ituner"))
                .license(new License().name("MIT License").url("https://opensource.org/licenses/MIT")))
        .servers(
            List.of(
                new Server().url("http://localhost:8080").description("Development server"),
                new Server()
                    .url("https://spring-ituner.example.com")
                    .description("Production server (if deployed)")));
  }
}
