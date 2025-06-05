package fr.epsib3devc2.backend.controllers;

import fr.epsib3devc2.backend.bo.CovidPrediction;
import fr.epsib3devc2.backend.repositories.PredictionCovidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/predictions")
public class PredictionCovidController {

    @Autowired
    private PredictionCovidRepository predictionRepository;

    // Garde la méthode originale pour compatibilité
    @GetMapping
    public List<CovidPrediction> getAllPredictions() {
        return predictionRepository.findAll();
    }

    // Ajoute une méthode avec pagination et format JSON simplifié
    @GetMapping("/paged")
    public Map<String, Object> getPagedPredictions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<CovidPrediction> predictionPage = predictionRepository.findAll(
                PageRequest.of(page, size));

        Map<String, Object> response = new HashMap<>();
        response.put("predictions", predictionPage.getContent());
        response.put("currentPage", predictionPage.getNumber());
        response.put("totalItems", predictionPage.getTotalElements());
        response.put("totalPages", predictionPage.getTotalPages());

        return response;
    }
}