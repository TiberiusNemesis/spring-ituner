package com.solo.learning.tdourado.configuration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SpringITunerConfiguration {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplateBuilder().build();
  }
}
