package com.service.texteslegaux.dto.piste;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PisteSection(
        String id,
        String title,
        String dateVersion,
        String legalStatus,
        List<PisteExtract> extracts
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PisteExtract(
            String id,
            String title,
            String legalStatus,
            String dateVersion,
            String dateDebut,
            String dateFin,
            String searchFieldName,
            String num,
            List<String> values,
            String type
    ) {
    }
}