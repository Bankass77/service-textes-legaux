package com.service.texteslegaux.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;


/**
 * Actif uniquement quand le vrai client PISTE est activé (profil "piste-reel").
 * OAuth2AuthorizedClientManager gère lui-même le cache et le renouvellement du
 * token client_credentials — pas besoin de coder ça à la main.
 */

@Configuration
@Profile("piste-sandbox")
public class PisteOAuth2Config {

    @Bean
    public OAuth2AuthorizedClientManager pisteOAuth2AuthorizedClientManager(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientService oAuth2AuthorizedClientService) {

        OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().build();
        AuthorizedClientServiceOAuth2AuthorizedClientManager manager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository,
                oAuth2AuthorizedClientService);

        manager.setAuthorizedClientProvider(authorizedClientProvider);
        return manager;
    }
}
