package com.service.texteslegaux.client;


import com.service.texteslegaux.dto.TexteLegalResponse;

import java.util.List;

/**
 * Le contrôleur ne dépend que de cette interface, jamais de l'implémentation
 * concrète. Le jour où tu as un compte PISTE, tu crées une PisteApiClient
 * (vrais appels HTTP + OAuth2 client_credentials vers piste.gouv.fr) et tu
 * la déclares comme le bean actif à la place de StubPisteClient — aucune
 * autre classe du service n'a besoin de changer.
 */
public interface PisteClient {

    List<TexteLegalResponse> rechercher(String q);
}
