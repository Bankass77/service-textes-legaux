package com.service.texteslegaux.dto.piste;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PisteResult(
        List<PisteTitle> titles,
        List<PisteSection> sections
) {
}