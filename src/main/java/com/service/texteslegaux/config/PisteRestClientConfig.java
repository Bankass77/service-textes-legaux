package com.service.texteslegaux.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestClient;

@Configuration
@Profile("piste-sandbox")
public class PisteRestClientConfig {

    @Bean
    public RestClient pisteRestClient(@Value("${services.piste.base-url}") String baseUrl) {
        System.out.println(">>> PISTE BASE URL = " + baseUrl);
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}