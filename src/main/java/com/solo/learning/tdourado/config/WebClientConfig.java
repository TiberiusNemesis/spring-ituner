package com.solo.learning.tdourado.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for WebClient setup.
 *
 * <p>Provides a WebClient.Builder bean for dependency injection into controllers and services.
 */
@Configuration
public class WebClientConfig {

  /**
   * Provides a WebClient.Builder bean for creating WebClient instances.
   *
   * @return A new WebClient.Builder
   */
  @Bean
  public WebClient.Builder webClientBuilder() {
    return WebClient.builder();
  }
}
