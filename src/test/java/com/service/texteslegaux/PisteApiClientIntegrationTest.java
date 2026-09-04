package com.service.texteslegaux;

import com.service.texteslegaux.client.PisteClient;
import com.service.texteslegaux.dto.TexteLegalResponse;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("piste-sandbox")
class PisteApiClientIntegrationTest {

    @Autowired
    private PisteClient pisteClient;

    @Test
    void rechercher_doit_interroger_reellement_piste() {

        // -----------------------------------------------------------------
        // GIVEN
        // -----------------------------------------------------------------

        String recherche = "référé suspension";

        // -----------------------------------------------------------------
        // WHEN
        // -----------------------------------------------------------------

        List<TexteLegalResponse> resultats =
                pisteClient.rechercher(recherche);

        // -----------------------------------------------------------------
        // THEN
        // -----------------------------------------------------------------

        assertThat(resultats)
                .isNotNull();

        assertThat(resultats)
                .isNotEmpty();

        assertThat(resultats)
                .allSatisfy(article -> {

                    assertThat(article.id())
                            .isNotBlank();

                    assertThat(article.code())
                            .isNotBlank();

                    assertThat(article.numeroArticle())
                            .isNotBlank();

                    assertThat(article.titre())
                            .isNotBlank();

                    assertThat(article.extrait())
                            .isNotBlank();

                    assertThat(article.dateVersion())
                            .isNotBlank();
                });
    }

    @Test
    void rechercher_doit_trouver_des_articles_du_code_de_justice_administrative() {

        // -----------------------------------------------------------------
        // GIVEN
        // -----------------------------------------------------------------

        String recherche = "référé suspension";

        // -----------------------------------------------------------------
        // WHEN
        // -----------------------------------------------------------------

        List<TexteLegalResponse> resultats = pisteClient.rechercher(recherche);

        // -----------------------------------------------------------------
        // THEN
        // -----------------------------------------------------------------

        assertThat(resultats)
                .isNotEmpty();

        assertThat(resultats)
                .allSatisfy(article -> {

                    assertThat(article.code())
                            .isEqualTo("LEGITEXT000006070933");

                    assertThat(article.titre())
                            .isEqualTo("Code de justice administrative");
                });
    }

    @Test
    void rechercher_doit_retourner_les_extraits_avec_les_marques_de_recherche() {

        // -----------------------------------------------------------------
        // GIVEN
        // -----------------------------------------------------------------

        String recherche = "suspension";

        // -----------------------------------------------------------------
        // WHEN
        // -----------------------------------------------------------------

        List<TexteLegalResponse> resultats = pisteClient.rechercher(recherche);

        // -----------------------------------------------------------------
        // THEN
        // -----------------------------------------------------------------

        assertThat(resultats)
                .isNotEmpty();

        assertThat(resultats)
                .anySatisfy(article ->
                        assertThat(article.extrait())
                                .contains("<mark>")
                );
    }
}