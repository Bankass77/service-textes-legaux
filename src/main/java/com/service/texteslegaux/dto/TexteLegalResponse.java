package com.service.texteslegaux.dto;

public record TexteLegalResponse(
        String id,
        String code,
        String numeroArticle,
        String titre,
        String extrait,
        String dateVersion
) {
}