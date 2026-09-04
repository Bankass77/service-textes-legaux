package com.service.texteslegaux.client;

import com.service.texteslegaux.dto.TexteLegalResponse;
import com.service.texteslegaux.dto.piste.PisteResult;
import com.service.texteslegaux.dto.piste.PisteSearchResponse;
import com.service.texteslegaux.dto.piste.PisteSection;
import com.service.texteslegaux.dto.piste.PisteTitle;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
@Profile("piste-sandbox")
@RequiredArgsConstructor
public class PisteApiClient implements PisteClient {

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    private final RestClient pisteRestClient;

    @Override
    public List<TexteLegalResponse> rechercher(String q) {

        String token = obtenirToken();

        Map<String, Object> corps = construireCorpsRecherche(q);

        PisteSearchResponse response = pisteRestClient.post()
                .uri("/search")
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + token
                )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(corps)
                .retrieve()
                .body(PisteSearchResponse.class);

        return convertir(response);
    }

    /**
     * Construit le payload attendu par l'API PISTE /search.
     */
    private Map<String, Object> construireCorpsRecherche(String q) {

        return Map.of(
                "recherche", Map.of(
                        "champs", List.of(
                                Map.of(
                                        "typeChamp", "ALL",
                                        "criteres", List.of(
                                                Map.of(
                                                        "typeRecherche", "UN_DES_MOTS",
                                                        "valeur", q,
                                                        "operateur", "ET"
                                                )
                                        ),
                                        "operateur", "ET"
                                )
                        ),
                        "filtres", List.of(
                                Map.of(
                                        "facette", "NOM_CODE",
                                        "valeurs", List.of(
                                                "Code de justice administrative"
                                        )
                                )
                        ),
                        "pageNumber", 1,
                        "pageSize", 10,
                        "operateur", "ET",
                        "sort", "PERTINENCE",
                        "typePagination", "DEFAUT"
                ),
                "fond", "CODE_DATE"
        );
    }

    /**
     * Récupère un access token OAuth2 auprès de PISTE.
     */
    private String obtenirToken() {

        OAuth2AuthorizeRequest request =
                OAuth2AuthorizeRequest
                        .withClientRegistrationId("piste")
                        .principal("service-textes-legaux")
                        .build();

        OAuth2AuthorizedClient client =
                authorizedClientManager.authorize(request);

        if (client == null) {
            throw new IllegalStateException("Impossible d'obtenir le client OAuth2 PISTE"
            );
        }

        OAuth2AccessToken accessToken = client.getAccessToken();

        if (accessToken == null) {
            throw new IllegalStateException("Aucun access token OAuth2 PISTE disponible"
            );
        }

        return accessToken.getTokenValue();
    }

    /**
     * Transforme la réponse PISTE en DTO métier TexteLegalResponse.
     * <p>
     * Structure PISTE :
     * <p>
     * results
     * └── titles
     * └── sections
     * └── extracts
     */
    private List<TexteLegalResponse> convertir(
            PisteSearchResponse response
    ) {

        if (response == null || response.results() == null) {
            return List.of();
        }

        return response.results()
                .stream()
                .flatMap(this::extraireArticles)
                .toList();
    }

    /**
     * Extrait les articles d'un résultat PISTE.
     */
    private Stream<TexteLegalResponse> extraireArticles(
            PisteResult result
    ) {

        if (result == null) {
            return Stream.empty();
        }

        String code = extraireCode(result);
        String titre = extraireTitre(result);

        if (result.sections() == null) {
            return Stream.empty();
        }

        return result.sections()
                .stream()
                .flatMap(section ->
                        extraireSection(
                                section,
                                code,
                                titre
                        )
                );
    }

    /**
     * Extrait les articles contenus dans une section.
     */
    private Stream<TexteLegalResponse> extraireSection(
            PisteSection section,
            String code,
            String titre
    ) {

        if (section == null || section.extracts() == null) {
            return Stream.empty();
        }

        return section.extracts()
                .stream()
                .map(extract -> {

                    String extrait = extraireValeur(extract);

                    return new TexteLegalResponse(
                            extract.id(),
                            code,
                            extract.num(),
                            titre,
                            extrait,
                            extract.dateVersion()
                    );
                });
    }

    /**
     * Récupère le CID du code.
     * <p>
     * Exemple :
     * LEGITEXT000006070933
     */
    private String extraireCode(PisteResult result) {

        if (result.titles() == null ||
                result.titles().isEmpty()) {

            return null;
        }

        PisteTitle title = result.titles().get(0);

        return title.cid();
    }

    /**
     * Récupère le nom du code.
     * <p>
     * Exemple :
     * Code de justice administrative
     */
    private String extraireTitre(PisteResult result) {

        if (result.titles() == null ||
                result.titles().isEmpty()) {

            return null;
        }

        PisteTitle title = result.titles().get(0);

        return title.title();
    }

    /**
     * Les valeurs de l'extrait sont retournées par PISTE
     * sous forme de liste.
     * <p>
     * Exemple :
     * <p>
     * [
     * "[...] <mark>suspension</mark> ... <mark>référé</mark> ..."
     * ]
     */
    private String extraireValeur(
            PisteSection.PisteExtract extract
    ) {

        if (extract == null ||
                extract.values() == null ||
                extract.values().isEmpty()) {

            return null;
        }

        return String.join(" ", extract.values());
    }
}