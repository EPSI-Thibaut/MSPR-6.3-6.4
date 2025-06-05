package fr.epsib3devc2.backend.controllers;

import fr.epsib3devc2.backend.bo.Continents;
import fr.epsib3devc2.backend.bo.Pandemics;
import fr.epsib3devc2.backend.bo.Regions;
import fr.epsib3devc2.backend.bo.TotalByDay;
import fr.epsib3devc2.backend.repositories.ContinentsRepository;
import fr.epsib3devc2.backend.repositories.PandemicsRepository;
import fr.epsib3devc2.backend.repositories.RegionsRepository;
import fr.epsib3devc2.backend.repositories.TotalByDayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class PandemicStatsController {

    @Autowired
    private TotalByDayRepository totalByDayRepository;

    @Autowired
    private PandemicsRepository pandemicsRepository;

    @Autowired
    private RegionsRepository regionsRepository;

    @Autowired
    private ContinentsRepository continentsRepository;

    @GetMapping("/stats/by-pandemic/{pandemicId}")
    public List<Map<String, Object>> getStatsByPandemic(@PathVariable Integer pandemicId) {
        System.out.println("Récupération des stats pour la pandémie ID: " + pandemicId);
        List<Map<String, Object>> result = new ArrayList<>();

        Optional<Pandemics> pandemic = pandemicsRepository.findById(pandemicId.longValue());
        if (!pandemic.isPresent()) {
            System.out.println("Pandémie non trouvée avec ID: " + pandemicId);
            return result;
        }

        System.out.println("Pandémie trouvée: " + pandemic.get().getName());
        List<Regions> regions = regionsRepository.findAll();
        System.out.println("Nombre total de régions: " + regions.size());

        int countWithData = 0;
        for (Regions region : regions) {
            List<TotalByDay> stats = totalByDayRepository.findLatestByPandemicAndRegion(
                    pandemicId.longValue(), region.getIdRegions());

            if (!stats.isEmpty()) {
                countWithData++;
                TotalByDay latestStat = stats.get(0);

                Map<String, Object> regionData = new HashMap<>();
                regionData.put("region", region.getName());
                regionData.put("regionId", region.getIdRegions());
                regionData.put("cases", latestStat.getCaseCount());
                regionData.put("deaths", latestStat.getDeath());
                regionData.put("recovered", latestStat.getRecovered());
                regionData.put("date", latestStat.getId().getDate());

                result.add(regionData);
            }
        }

        System.out.println("Régions avec données: " + countWithData);
        return result;
    }

    @GetMapping("/stats/by-region/{regionId}")
    public List<Map<String, Object>> getStatsByRegion(@PathVariable Integer regionId) {
        System.out.println("Récupération des stats pour la région ID: " + regionId);
        List<Map<String, Object>> result = new ArrayList<>();

        Optional<Regions> region = regionsRepository.findById(regionId);
        if (!region.isPresent()) {
            System.out.println("Région non trouvée avec ID: " + regionId);
            return result;
        }

        System.out.println("Région trouvée: " + region.get().getName());
        List<Pandemics> pandemics = pandemicsRepository.findAll();
        System.out.println("Nombre total de pandémies: " + pandemics.size());

        int countWithData = 0;
        for (Pandemics pandemic : pandemics) {
            List<TotalByDay> stats = totalByDayRepository.findLatestByPandemicAndRegion(
                    pandemic.getIdPandemics(), regionId);

            if (!stats.isEmpty()) {
                countWithData++;
                TotalByDay latestStat = stats.get(0);

                Map<String, Object> pandemicData = new HashMap<>();
                pandemicData.put("pandemic", pandemic.getName());
                pandemicData.put("pandemicId", pandemic.getIdPandemics());
                pandemicData.put("cases", latestStat.getCaseCount());
                pandemicData.put("deaths", latestStat.getDeath());
                pandemicData.put("recovered", latestStat.getRecovered());
                pandemicData.put("date", latestStat.getId().getDate());

                result.add(pandemicData);
            }
        }

        System.out.println("Pandémies avec données pour la région: " + countWithData);
        return result;
    }

    @GetMapping("/stats/timeline/{pandemicId}/{regionId}")
    public List<Map<String, Object>> getTimelineStats(
            @PathVariable Integer pandemicId,
            @PathVariable Integer regionId) {
        System.out.println("Récupération de la timeline pour pandémie ID: " + pandemicId + " et région ID: " + regionId);
        List<Map<String, Object>> result = new ArrayList<>();

        Optional<Regions> region = regionsRepository.findById(regionId);
        Optional<Pandemics> pandemic = pandemicsRepository.findById(pandemicId.longValue());

        if (!region.isPresent() || !pandemic.isPresent()) {
            System.out.println("Région ou pandémie non trouvée");
            return result;
        }

        System.out.println("Région: " + region.get().getName() + ", Pandémie: " + pandemic.get().getName());
        List<TotalByDay> allStats = totalByDayRepository.findByPandemicAndRegionOrderByDate(
                pandemicId, regionId);

        System.out.println("Nombre de points de données trouvés: " + allStats.size());
        for (TotalByDay stat : allStats) {
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", stat.getId().getDate());
            dayData.put("cases", stat.getCaseCount());
            dayData.put("deaths", stat.getDeath());
            dayData.put("recovered", stat.getRecovered());

            result.add(dayData);
        }

        return result;
    }

    @GetMapping("/stats/compare/{pandemic1Id}/{pandemic2Id}/{regionId}")
    public Map<String, Object> comparePandemics(
            @PathVariable Integer pandemic1Id,
            @PathVariable Integer pandemic2Id,
            @PathVariable Integer regionId) {
        System.out.println("Comparaison des pandémies ID: " + pandemic1Id + " et " + pandemic2Id + " pour la région ID: " + regionId);
        Map<String, Object> result = new HashMap<>();

        Optional<Regions> region = regionsRepository.findById(regionId);
        Optional<Pandemics> pandemic1 = pandemicsRepository.findById(pandemic1Id.longValue());
        Optional<Pandemics> pandemic2 = pandemicsRepository.findById(pandemic2Id.longValue());

        if (!region.isPresent() || !pandemic1.isPresent() || !pandemic2.isPresent()) {
            System.out.println("Région ou pandémie non trouvée");
            return result;
        }

        System.out.println("Région: " + region.get().getName());
        System.out.println("Pandémie 1: " + pandemic1.get().getName());
        System.out.println("Pandémie 2: " + pandemic2.get().getName());

        List<TotalByDay> stats1 = totalByDayRepository.findByPandemicAndRegionOrderByDate(
                pandemic1Id, regionId);

        List<TotalByDay> stats2 = totalByDayRepository.findByPandemicAndRegionOrderByDate(
                pandemic2Id, regionId);

        System.out.println("Points de données pour pandémie 1: " + stats1.size());
        System.out.println("Points de données pour pandémie 2: " + stats2.size());

        result.put("region", region.get().getName());
        result.put("pandemic1", pandemic1.get().getName());
        result.put("pandemic2", pandemic2.get().getName());

        if (!stats1.isEmpty()) {
            TotalByDay maxStats1 = stats1.stream()
                    .max(Comparator.comparing(TotalByDay::getCaseCount))
                    .orElse(stats1.get(stats1.size() - 1));

            Map<String, Object> maxData1 = new HashMap<>();
            maxData1.put("cases", maxStats1.getCaseCount());
            maxData1.put("deaths", maxStats1.getDeath());
            maxData1.put("recovered", maxStats1.getRecovered());
            maxData1.put("date", maxStats1.getId().getDate());

            result.put("pandemic1MaxStats", maxData1);
        }

        if (!stats2.isEmpty()) {
            TotalByDay maxStats2 = stats2.stream()
                    .max(Comparator.comparing(TotalByDay::getCaseCount))
                    .orElse(stats2.get(stats2.size() - 1));

            Map<String, Object> maxData2 = new HashMap<>();
            maxData2.put("cases", maxStats2.getCaseCount());
            maxData2.put("deaths", maxStats2.getDeath());
            maxData2.put("recovered", maxStats2.getRecovered());
            maxData2.put("date", maxStats2.getId().getDate());

            result.put("pandemic2MaxStats", maxData2);
        }

        // Ajouter des métriques comparatives si les deux pandémies ont des données
        if (!stats1.isEmpty() && !stats2.isEmpty()) {
            TotalByDay maxStats1 = stats1.stream()
                    .max(Comparator.comparing(TotalByDay::getCaseCount))
                    .get();
            TotalByDay maxStats2 = stats2.stream()
                    .max(Comparator.comparing(TotalByDay::getCaseCount))
                    .get();

            Map<String, Object> comparisons = new HashMap<>();
            double caseRatio = maxStats2.getCaseCount() > 0 ?
                    (double) maxStats1.getCaseCount() / maxStats2.getCaseCount() : 0;
            double deathRatio = maxStats2.getDeath() > 0 ?
                    (double) maxStats1.getDeath() / maxStats2.getDeath() : 0;

            double mortalityRate1 = maxStats1.getCaseCount() > 0 ?
                    (double) maxStats1.getDeath() * 100 / maxStats1.getCaseCount() : 0;
            double mortalityRate2 = maxStats2.getCaseCount() > 0 ?
                    (double) maxStats2.getDeath() * 100 / maxStats2.getCaseCount() : 0;

            comparisons.put("caseRatio", caseRatio);
            comparisons.put("deathRatio", deathRatio);
            comparisons.put("mortalityRate1", mortalityRate1);
            comparisons.put("mortalityRate2", mortalityRate2);

            result.put("comparisonMetrics", comparisons);
        }

        return result;
    }

    @GetMapping("/pandemics")
    public List<Map<String, Object>> getAllPandemicsWithStats() {
        System.out.println("Récupération de toutes les pandémies avec statistiques");
        List<Map<String, Object>> result = new ArrayList<>();
        List<Pandemics> pandemics = pandemicsRepository.findAll();

        System.out.println("Nombre de pandémies trouvées: " + pandemics.size());
        for (Pandemics pandemic : pandemics) {
            Map<String, Object> pandemicData = new HashMap<>();
            pandemicData.put("id", pandemic.getIdPandemics());
            pandemicData.put("name", pandemic.getName());

            Long totalCases = totalByDayRepository.sumCasesByPandemic(pandemic.getIdPandemics());
            Long totalDeaths = totalByDayRepository.sumDeathsByPandemic(pandemic.getIdPandemics());
            Long affectedRegions = totalByDayRepository.countDistinctRegionsByPandemic(pandemic.getIdPandemics());

            pandemicData.put("totalCases", totalCases);
            pandemicData.put("totalDeaths", totalDeaths);
            pandemicData.put("mortalityRate", totalCases > 0 ? (totalDeaths.doubleValue() / totalCases.doubleValue()) * 100 : 0);
            pandemicData.put("affectedRegions", affectedRegions);

            result.add(pandemicData);
        }

        return result;
    }

    @GetMapping("/stats/continents-comparison")
    public List<Map<String, Object>> getContinentsComparison() {
        System.out.println("Récupération des statistiques de tous les continents pour comparaison");
        List<Map<String, Object>> result = new ArrayList<>();

        // Récupérer tous les continents
        List<Continents> allContinents = continentsRepository.findAll();
        System.out.println("Nombre de continents trouvés: " + allContinents.size());

        for (Continents continent : allContinents) {
            Map<String, Object> continentData = new HashMap<>();
            continentData.put("continentId", continent.getIdContinents());
            continentData.put("continentName", continent.getName());

            // Obtenir les statistiques pour ce continent
            Map<String, Object> continentStats = getStatsForContinent(continent);
            continentData.putAll(continentStats);

            result.add(continentData);
        }

        return result;
    }

    @GetMapping("/regions")
    public List<Map<String, Object>> getAllRegionsWithContinents() {
        System.out.println("Récupération de toutes les régions avec continents");
        List<Map<String, Object>> result = new ArrayList<>();
        List<Regions> regions = regionsRepository.findAll();

        System.out.println("Nombre de régions trouvées: " + regions.size());
        for (Regions region : regions) {
            Map<String, Object> regionData = new HashMap<>();
            regionData.put("id", region.getIdRegions());
            regionData.put("name", region.getName());

            if (region.getContinent() != null) {
                regionData.put("continentId", region.getContinent().getIdContinents());
                regionData.put("continentName", region.getContinent().getName());
            }

            // Vérifier si la région a des données
            long dataCount = totalByDayRepository.countByRegionId(region.getIdRegions());
            regionData.put("hasData", dataCount > 0);
            regionData.put("dataCount", dataCount);

            result.add(regionData);
        }

        return result;
    }

    @GetMapping("/debug/totalbydays")
    public List<Map<String, Object>> getDebugTotalByDays(
            @RequestParam(required = false) Integer limit) {
        int maxResults = limit != null ? limit : 100;
        List<Map<String, Object>> result = new ArrayList<>();

        List<TotalByDay> data = totalByDayRepository.findAll();
        System.out.println("Total des enregistrements dans total_by_day: " + data.size());

        int count = 0;
        for (TotalByDay record : data) {
            if (count >= maxResults) break;

            Map<String, Object> item = new HashMap<>();
            item.put("pandemicId", record.getPandemics().getIdPandemics());
            item.put("pandemicName", record.getPandemics().getName());
            item.put("regionId", record.getRegions().getIdRegions());
            item.put("regionName", record.getRegions().getName());
            item.put("date", record.getId().getDate());
            item.put("cases", record.getCaseCount());
            item.put("deaths", record.getDeath());
            item.put("recovered", record.getRecovered());

            result.add(item);
            count++;
        }

        return result;
    }

    @GetMapping("/stats/by-continent/{continentId}")
    public Map<String, Object> getStatsByContinent(@PathVariable Integer continentId) {
        System.out.println("Récupération des statistiques pour le continent ID: " + continentId);
        Map<String, Object> result = new HashMap<>();

        // Récupérer le continent
        Optional<Continents> continentOpt = continentsRepository.findById(continentId);
        if (!continentOpt.isPresent()) {
            System.out.println("Continent non trouvé avec ID: " + continentId);
            return result;
        }

        Continents continent = continentOpt.get();
        System.out.println("Continent trouvé: " + continent.getName());

        // Données de base du continent
        result.put("continentId", continent.getIdContinents());
        result.put("continentName", continent.getName());

        // Obtenir toutes les statistiques pour ce continent
        Map<String, Object> continentStats = getStatsForContinent(continent);
        result.putAll(continentStats);

        return result;
    }

    // Méthode utilitaire pour créer des statistiques vides
    private Map<String, Object> createEmptyStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCases", 0L);
        stats.put("totalDeaths", 0L);
        stats.put("totalRecovered", 0L);
        stats.put("mortalityRate", 0.0);
        stats.put("affectedRegions", 0L);
        return stats;
    }

    // Méthode utilitaire pour obtenir les statistiques complètes d'un continent
    private Map<String, Object> getStatsForContinent(Continents continent) {
        Map<String, Object> result = new HashMap<>();

        // Obtenir toutes les régions de ce continent
        List<Regions> regionsInContinent = regionsRepository.findByContinent(continent);
        System.out.println("Nombre de régions pour le continent " + continent.getName() + ": " + regionsInContinent.size());

        if (regionsInContinent.isEmpty()) {
            System.out.println("Aucune région trouvée pour ce continent.");
            result.put("covid", createEmptyStats());
            result.put("sars", createEmptyStats());
            result.put("regions", new ArrayList<>());
            return result;
        }

        List<Integer> regionIds = regionsInContinent.stream()
                .map(Regions::getIdRegions)
                .collect(Collectors.toList());

        // Statistiques pour le COVID-19 et SARS
        Map<String, Object> covidStats = calculatePandemicStatsForRegions(2L, regionIds);
        Map<String, Object> sarsStats = calculatePandemicStatsForRegions(1L, regionIds);

        result.put("covid", covidStats);
        result.put("sars", sarsStats);
        result.put("comparison", calculatePandemicComparison(covidStats, sarsStats));
        result.put("regions", buildRegionsDataForContinent(regionsInContinent));

        return result;
    }

    // Méthode utilitaire pour calculer la comparaison entre pandémies
    private Map<String, Object> calculatePandemicComparison(Map<String, Object> covidStats, Map<String, Object> sarsStats) {
        Map<String, Object> comparison = new HashMap<>();

        // Calculer les ratios entre COVID et SARS
        Long covidCases = (Long) covidStats.get("totalCases");
        Long sarsCases = (Long) sarsStats.get("totalCases");
        if (covidCases > 0 && sarsCases > 0) {
            comparison.put("casesRatio", (double) covidCases / sarsCases);
        }

        Long covidDeaths = (Long) covidStats.get("totalDeaths");
        Long sarsDeaths = (Long) sarsStats.get("totalDeaths");
        if (covidDeaths > 0 && sarsDeaths > 0) {
            comparison.put("deathsRatio", (double) covidDeaths / sarsDeaths);
        }

        // Taux de mortalité des deux pandémies
        Double covidMortalityRate = (Double) covidStats.get("mortalityRate");
        Double sarsMortalityRate = (Double) sarsStats.get("mortalityRate");
        if (covidMortalityRate != null && sarsMortalityRate != null) {
            comparison.put("mortalityRateDifference", sarsMortalityRate - covidMortalityRate);
        }

        return comparison;
    }

    // Méthode utilitaire pour construire les données des régions d'un continent
    private List<Map<String, Object>> buildRegionsDataForContinent(List<Regions> regionsInContinent) {
        List<Map<String, Object>> regionsData = new ArrayList<>();

        for (Regions region : regionsInContinent) {
            Map<String, Object> regionData = new HashMap<>();
            regionData.put("regionId", region.getIdRegions());
            regionData.put("regionName", region.getName());

            // Statistiques COVID pour cette région
            List<TotalByDay> covidStatsForRegion = totalByDayRepository.findLatestByPandemicAndRegion(87497L, region.getIdRegions());
            if (!covidStatsForRegion.isEmpty()) {
                TotalByDay latestCovidStat = covidStatsForRegion.get(0);
                Map<String, Object> regionCovidData = new HashMap<>();
                regionCovidData.put("cases", latestCovidStat.getCaseCount());
                regionCovidData.put("deaths", latestCovidStat.getDeath());
                regionCovidData.put("recovered", latestCovidStat.getRecovered());
                regionCovidData.put("date", latestCovidStat.getId().getDate());
                regionData.put("covid", regionCovidData);
            }

            // Statistiques SARS pour cette région
            List<TotalByDay> sarsStatsForRegion = totalByDayRepository.findLatestByPandemicAndRegion(87495L, region.getIdRegions());
            if (!sarsStatsForRegion.isEmpty()) {
                TotalByDay latestSarsStat = sarsStatsForRegion.get(0);
                Map<String, Object> regionSarsData = new HashMap<>();
                regionSarsData.put("cases", latestSarsStat.getCaseCount());
                regionSarsData.put("deaths", latestSarsStat.getDeath());
                regionSarsData.put("recovered", latestSarsStat.getRecovered());
                regionSarsData.put("date", latestSarsStat.getId().getDate());
                regionData.put("sars", regionSarsData);
            }

            // N'ajouter que les régions qui ont des données pour au moins une pandémie
            if (regionData.containsKey("covid") || regionData.containsKey("sars")) {
                regionsData.add(regionData);
            }
        }

        return regionsData;
    }

    // Méthode utilitaire pour calculer les statistiques d'une pandémie sur un ensemble de régions
    private Map<String, Object> calculatePandemicStatsForRegions(Long pandemicId, List<Integer> regionIds) {
        Map<String, Object> stats = new HashMap<>();

        // Valeurs par défaut
        stats.put("totalCases", 0L);
        stats.put("totalDeaths", 0L);
        stats.put("totalRecovered", 0L);
        stats.put("mortalityRate", 0.0);
        stats.put("affectedRegions", 0L);

        // Si aucune région, retourner des statistiques vides
        if (regionIds.isEmpty()) {
            return stats;
        }

        // Compteurs pour les statistiques
        long totalCases = 0;
        long totalDeaths = 0;
        long totalRecovered = 0;
        long affectedRegions = 0;

        for (Integer regionId : regionIds) {
            List<TotalByDay> latestStats = totalByDayRepository.findLatestByPandemicAndRegion(pandemicId, regionId);

            if (!latestStats.isEmpty()) {
                TotalByDay latestStat = latestStats.get(0);

                totalCases += latestStat.getCaseCount();
                totalDeaths += latestStat.getDeath();
                totalRecovered += latestStat.getRecovered();
                affectedRegions++;
            }
        }

        // Calculer le taux de mortalité
        double mortalityRate = totalCases > 0 ? (double) totalDeaths * 100 / totalCases : 0;

        stats.put("totalCases", totalCases);
        stats.put("totalDeaths", totalDeaths);
        stats.put("totalRecovered", totalRecovered);
        stats.put("mortalityRate", mortalityRate);
        stats.put("affectedRegions", affectedRegions);

        return stats;
    }
}