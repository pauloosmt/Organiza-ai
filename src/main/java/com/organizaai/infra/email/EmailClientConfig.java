package com.organizaai.infra.email;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class EmailClientConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }
}
