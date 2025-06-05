package fr.epsib3devc2.backend.controllers;

import fr.epsib3devc2.backend.bo.Continents;
import fr.epsib3devc2.backend.bo.Pandemics;
import fr.epsib3devc2.backend.repositories.ContinentsRepository;
import fr.epsib3devc2.backend.repositories.PandemicsRepository;
import fr.epsib3devc2.backend.repositories.RegionsRepository;
import fr.epsib3devc2.backend.repositories.TotalByDayRepository;
import fr.epsib3devc2.backend.services.DataInsertionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/analysis")
public class PandemicAnalysisController {

    @Autowired
    private TotalByDayRepository totalByDayRepository;
    
    @Autowired
    private PandemicsRepository pandemicsRepository;
    
    @Autowired
    private ContinentsRepository continentsRepository;
    
    @Autowired
    private DataInsertionService dataInsertionService;

    @GetMapping("/pandemics")
    public List<Pandemics> getAllPandemics() {
        return pandemicsRepository.findAll();
    }
    
    @GetMapping("/continents")
    public List<Continents> getAllContinents() {
        return continentsRepository.findAll();
    }
    
    @GetMapping("/comparative")
    public Map<String, Object> getComparativeAnalysis() {
        return dataInsertionService.analyzeComparativePandemicData();
    }
    
    @GetMapping("/pandemic/{pandemicId}/by-continent")
    public Map<String, Object> getPandemicStatsByContinent(@PathVariable Integer pandemicId) {
        Map<String, Object> result = new HashMap<>();
        
        Optional<Pandemics> pandemic = pandemicsRepository.findById(pandemicId.longValue());
        if (!pandemic.isPresent()) {
            return result;
        }
        
        result.put("pandemic", pandemic.get().getName());
        
        List<Continents> continents = continentsRepository.findAll();
        List<Map<String, Object>> continentStats = new ArrayList<>();
        
        for (Continents continent : continents) {
            Map<String, Object> stats = new HashMap<>();
            stats.put("continent", continent.getName());
            
            Long totalCases = totalByDayRepository.sumCasesByPandemicAndContinent(pandemicId, continent.getIdContinents());
            Long totalDeaths = totalByDayRepository.sumDeathsByPandemicAndContinent(pandemicId, continent.getIdContinents());
            
            stats.put("totalCases", totalCases);
            stats.put("totalDeaths", totalDeaths);
            stats.put("mortalityRate", totalCases > 0 ? (totalDeaths.doubleValue() / totalCases.doubleValue()) * 100 : 0);
            
            continentStats.add(stats);
        }
        
        result.put("continentStats", continentStats);
        return result;
    }
}