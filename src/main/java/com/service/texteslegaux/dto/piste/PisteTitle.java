package com.service.texteslegaux.dto.piste;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PisteTitle(
        String id,
        String cid,
        String title,
        String legalStatus,
        String startDate,
        String endDate,
        String nature
) {
}