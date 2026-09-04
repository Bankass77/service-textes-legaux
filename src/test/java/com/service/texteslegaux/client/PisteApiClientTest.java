package com.service.texteslegaux.client;

import com.service.texteslegaux.dto.TexteLegalResponse;
import com.service.texteslegaux.dto.piste.PisteResult;
import com.service.texteslegaux.dto.piste.PisteSearchResponse;
import com.service.texteslegaux.dto.piste.PisteSection;
import com.service.texteslegaux.dto.piste.PisteTitle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PisteApiClientTest {

    @Mock
    private OAuth2AuthorizedClientManager authorizedClientManager;

    @Mock
    private RestClient pisteRestClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Mock
    private OAuth2AuthorizedClient authorizedClient;

    private PisteApiClient pisteApiClient;

    @BeforeEach
    void setUp() {
        pisteApiClient = new PisteApiClient(
                authorizedClientManager,
                pisteRestClient
        );
    }

    @Test
    void rechercher_doit_retourner_les_articles() {

        // -----------------------------------------------------------------
        // GIVEN
        // -----------------------------------------------------------------

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "fake-piste-token",
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );

        when(authorizedClientManager.authorize(any()))
                .thenReturn(authorizedClient);

        when(authorizedClient.getAccessToken())
                .thenReturn(accessToken);

        PisteSection.PisteExtract extract =
                new PisteSection.PisteExtract(
                        "LEGIARTI000051372410",
                        "L77-15-3",
                        "VIGUEUR",
                        "2025-03-26T00:00:00.000+0000",
                        "2025-03-26T00:00:00.000+0000",
                        "2999-01-01T00:00:00.000+0000",
                        null,
                        "L77-15-3",
                        List.of(
                                "[...] <mark>suspension</mark> " +
                                        "et le juge des <mark>référés</mark>."
                        ),
                        "articles"
                );

        PisteSection section = new PisteSection(
                "LEGISCTA000051372358",
                "Chapitre XV",
                "2025-03-26T00:00:00.000+0000",
                "VIGUEUR",
                List.of(extract)
        );

        PisteTitle title = new PisteTitle(
                "LEGITEXT000006070933_20-08-2026",
                "LEGITEXT000006070933",
                "Code de justice administrative",
                null,
                null,
                null,
                "code"
        );

        PisteResult result = new PisteResult(
                List.of(title),
                List.of(section)
        );

        PisteSearchResponse response =
                new PisteSearchResponse(List.of(result));


        mockRestClient(response);
        // -----------------------------------------------------------------
        // WHEN
        // -----------------------------------------------------------------

        List<TexteLegalResponse> resultats =
                pisteApiClient.rechercher("référé suspension");

        // -----------------------------------------------------------------
        // THEN
        // -----------------------------------------------------------------

        assertThat(resultats)
                .hasSize(1);

        TexteLegalResponse article = resultats.get(0);

        assertThat(article.id())
                .isEqualTo("LEGIARTI000051372410");

        assertThat(article.code())
                .isEqualTo("LEGITEXT000006070933");

        assertThat(article.numeroArticle())
                .isEqualTo("L77-15-3");

        assertThat(article.titre())
                .isEqualTo("Code de justice administrative");

        assertThat(article.extrait())
                .contains("<mark>suspension</mark>");

        assertThat(article.extrait())
                .contains("<mark>référés</mark>");

        assertThat(article.dateVersion())
                .isEqualTo("2025-03-26T00:00:00.000+0000");

        verify(authorizedClientManager)
                .authorize(any());

        verify(pisteRestClient)
                .post();

        verify(requestBodyUriSpec)
                .uri("/search");

        verify(requestBodySpec)
                .contentType(MediaType.APPLICATION_JSON);

        verify(requestBodySpec)
                .accept(MediaType.APPLICATION_JSON);

        verify(responseSpec)
                .body(PisteSearchResponse.class);
    }

    @Test
    void rechercher_doit_flattenir_plusieurs_sections_et_articles() {

        // -----------------------------------------------------------------
        // GIVEN
        // -----------------------------------------------------------------

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "fake-piste-token",
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );

        when(authorizedClientManager.authorize(any()))
                .thenReturn(authorizedClient);

        when(authorizedClient.getAccessToken())
                .thenReturn(accessToken);

        PisteTitle title = new PisteTitle(
                "LEGITEXT000006070933_20-08-2026",
                "LEGITEXT000006070933",
                "Code de justice administrative",
                null,
                null,
                null,
                "code"
        );

        PisteSection.PisteExtract article1 =
                new PisteSection.PisteExtract(
                        "LEGIARTI000000001",
                        "Article L1",
                        "VIGUEUR",
                        "2026-01-01",
                        null,
                        null,
                        null,
                        "L1",
                        List.of("Contenu article L1"),
                        "articles"
                );

        PisteSection.PisteExtract article2 =
                new PisteSection.PisteExtract(
                        "LEGIARTI000000002",
                        "Article L2",
                        "VIGUEUR",
                        "2026-01-01",
                        null,
                        null,
                        null,
                        "L2",
                        List.of("Contenu article L2"),
                        "articles"
                );

        PisteSection.PisteExtract article3 =
                new PisteSection.PisteExtract(
                        "LEGIARTI000000003",
                        "Article L3",
                        "VIGUEUR",
                        "2026-01-01",
                        null,
                        null,
                        null,
                        "L3",
                        List.of("Contenu article L3"),
                        "articles"
                );

        PisteSection section1 = new PisteSection(
                "SECTION1",
                "Section 1",
                "2026-01-01",
                "VIGUEUR",
                List.of(article1, article2)
        );

        PisteSection section2 = new PisteSection(
                "SECTION2",
                "Section 2",
                "2026-01-01",
                "VIGUEUR",
                List.of(article3)
        );

        PisteResult pisteResult = new PisteResult(
                List.of(title),
                List.of(section1, section2)
        );

        PisteSearchResponse response =
                new PisteSearchResponse(List.of(pisteResult));

        mockRestClient(response);

        // -----------------------------------------------------------------
        // WHEN
        // -----------------------------------------------------------------

        List<TexteLegalResponse> resultats =
                pisteApiClient.rechercher("suspension");

        // -----------------------------------------------------------------
        // THEN
        // -----------------------------------------------------------------

        assertThat(resultats)
                .hasSize(3);

        assertThat(resultats)
                .extracting(TexteLegalResponse::id)
                .containsExactly(
                        "LEGIARTI000000001",
                        "LEGIARTI000000002",
                        "LEGIARTI000000003"
                );

        assertThat(resultats)
                .extracting(TexteLegalResponse::numeroArticle)
                .containsExactly(
                        "L1",
                        "L2",
                        "L3"
                );

        assertThat(resultats)
                .allSatisfy(article -> {
                    assertThat(article.code())
                            .isEqualTo("LEGITEXT000006070933");

                    assertThat(article.titre())
                            .isEqualTo("Code de justice administrative");
                });
    }

    @Test
    void rechercher_doit_retourner_liste_vide_si_response_null() {

        // -----------------------------------------------------------------
        // GIVEN
        // -----------------------------------------------------------------

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "fake-piste-token",
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );

        when(authorizedClientManager.authorize(any()))
                .thenReturn(authorizedClient);

        when(authorizedClient.getAccessToken())
                .thenReturn(accessToken);

        mockRestClient(null);

        // -----------------------------------------------------------------
        // WHEN
        // -----------------------------------------------------------------

        List<TexteLegalResponse> resultats =
                pisteApiClient.rechercher("test");

        // -----------------------------------------------------------------
        // THEN
        // -----------------------------------------------------------------

        assertThat(resultats)
                .isEmpty();
    }

    @Test
    void rechercher_doit_retourner_liste_vide_si_aucun_resultat() {

        // -----------------------------------------------------------------
        // GIVEN
        // -----------------------------------------------------------------

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "fake-piste-token",
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );

        when(authorizedClientManager.authorize(any()))
                .thenReturn(authorizedClient);

        when(authorizedClient.getAccessToken())
                .thenReturn(accessToken);

        PisteSearchResponse response =
                new PisteSearchResponse(List.of());

        mockRestClient(response);

        // -----------------------------------------------------------------
        // WHEN
        // -----------------------------------------------------------------

        List<TexteLegalResponse> resultats =
                pisteApiClient.rechercher("mot-inexistant");

        // -----------------------------------------------------------------
        // THEN
        // -----------------------------------------------------------------

        assertThat(resultats)
                .isEmpty();
    }

    @Test
    void rechercher_doit_gerer_un_resultat_sans_sections() {

        // -----------------------------------------------------------------
        // GIVEN
        // -----------------------------------------------------------------

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "fake-piste-token",
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );

        when(authorizedClientManager.authorize(any()))
                .thenReturn(authorizedClient);

        when(authorizedClient.getAccessToken())
                .thenReturn(accessToken);

        PisteTitle title = new PisteTitle(
                "LEGITEXT000006070933_20-08-2026",
                "LEGITEXT000006070933",
                "Code de justice administrative",
                null,
                null,
                null,
                "code"
        );

        PisteResult result = new PisteResult(
                List.of(title),
                null
        );

        PisteSearchResponse response =
                new PisteSearchResponse(List.of(result));

        mockRestClient(response);

        // -----------------------------------------------------------------
        // WHEN
        // -----------------------------------------------------------------

        List<TexteLegalResponse> resultats =
                pisteApiClient.rechercher("test");

        // -----------------------------------------------------------------
        // THEN
        // -----------------------------------------------------------------

        assertThat(resultats)
                .isEmpty();
    }

    private void mockRestClient(PisteSearchResponse response) {

        when(pisteRestClient.post())
                .thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri("/search"))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.header(
                eq("Authorization"),
                anyString()
        )).thenReturn(requestBodySpec);

        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.accept(MediaType.APPLICATION_JSON))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.body(any(Object.class)))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.retrieve())
                .thenReturn(responseSpec);

        when(responseSpec.body(PisteSearchResponse.class))
                .thenReturn(response);
    }
}