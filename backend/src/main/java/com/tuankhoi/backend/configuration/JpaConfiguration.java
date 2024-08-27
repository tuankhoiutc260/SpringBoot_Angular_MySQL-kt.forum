package com.tuankhoi.backend.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.tuankhoi.backend.repository.Jpa")
public class JpaConfiguration {
}
