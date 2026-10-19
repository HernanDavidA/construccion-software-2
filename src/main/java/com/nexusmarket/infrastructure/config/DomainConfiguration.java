package com.nexusmarket.infrastructure.config;

import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfiguration {

    @Bean
    AccessPolicy accessPolicy() {
        return new AccessPolicy();
    }
}
