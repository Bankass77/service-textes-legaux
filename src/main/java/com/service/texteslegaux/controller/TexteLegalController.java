package com.service.texteslegaux.controller;

import com.service.texteslegaux.client.PisteClient;
import com.service.texteslegaux.dto.TexteLegalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/textes-legaux")
@RequiredArgsConstructor
public class TexteLegalController {

    private final PisteClient pisteClient;

    @GetMapping
    public List<TexteLegalResponse> rechercher(@RequestParam String q) {
        return pisteClient.rechercher(q);
    }
}