package fr.epsib3devc2.backend.services;

import fr.epsib3devc2.backend.bo.Continents;
import fr.epsib3devc2.backend.bo.Countries;
import fr.epsib3devc2.backend.bo.Pandemics;
import fr.epsib3devc2.backend.bo.Regions;
import fr.epsib3devc2.backend.bo.TotalByDay;
import fr.epsib3devc2.backend.bo.TotalByDayId;
import fr.epsib3devc2.backend.dto.CovidDto;
import fr.epsib3devc2.backend.dto.CovidDailyDto;
import fr.epsib3devc2.backend.dto.SarsDto;
import fr.epsib3devc2.backend.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataInsertionService {

    @Autowired
    private PandemicsRepository pandemicsRepository;

    @Autowired
    private TotalByDayRepository totalByDayRepository;

    @Autowired
    private CountriesRepository countriesRepository;

    @Autowired
    private ContinentsRepository continentsRepository;

    @Autowired
    private RegionsRepository regionsRepository;

    private Map<String, Pandemics> pandemicsMap = new HashMap<>();
    private Map<String, Regions> regionsMap = new HashMap<>();
    private Map<String, Continents> continentsMap = new HashMap<>();
    private Map<String, Countries> countriesMap = new HashMap<>();

    private Map<String, String> countryToContinentMap = new HashMap<>();

    /**
     * Initialise la base de données avec les pandémies et les régions
     */
    @Transactional
    public void initializeDatabase() {
        System.out.println("Initialisation des pandémies dans la base de données...");
    
        // Créer les deux types de pandémies (SARS et COVID-19)
        Pandemics sarsPandemic = createPandemic(DataNormalizationService.PANDEMIC_SARS);
        Pandemics covidPandemic = createPandemic(DataNormalizationService.PANDEMIC_COVID);
        
        // Vérifie que les noms sont bien assignés
        System.out.println("Pandémie SARS créée avec ID: " + sarsPandemic.getIdPandemics() + 
                           " et nom: " + sarsPandemic.getName());
        System.out.println("Pandémie COVID créée avec ID: " + covidPandemic.getIdPandemics() + 
                           " et nom: " + covidPandemic.getName());
    
        System.out.println("Pandémies initialisées avec succès.");
    }

    /**
     * Crée une entrée dans la table des pandémies si elle n'existe pas déjà
     */
    private Pandemics createPandemic(String name) {
        if (pandemicsMap.containsKey(name)) {
            return pandemicsMap.get(name);
        }

        List<Pandemics> existingPandemics = pandemicsRepository.findByName(name);
        Pandemics pandemic;

        if (existingPandemics.isEmpty()) {
            pandemic = new Pandemics();
            pandemic.setName(name);
            pandemic = pandemicsRepository.save(pandemic);
            System.out.println("Pandémie créée: " + name + " avec ID: " + pandemic.getIdPandemics());
        } else {
            pandemic = existingPandemics.get(0);
            System.out.println("Pandémie existante: " + name + " avec ID: " + pandemic.getIdPandemics());
        }

        pandemicsMap.put(name, pandemic);
        return pandemic;
    }

    /**
     * Extrait les associations pays-continent à partir des données COVID
     * Cette méthode doit être appelée avant createContinents() et prepareRegions()
     */
    @Transactional
    public void extractContinentData(List<CovidDto> covidData) {
        if (covidData == null) return;
        
        System.out.println("Extraction des associations pays-continent...");
        countryToContinentMap.clear(); // Vider la map avant de la remplir

        for (CovidDto dto : covidData) {
            if (dto.getCountry() != null && dto.getContinent() != null) {
                String normalizedCountry = DataNormalizationService.normalizeCountryName(dto.getCountry());
                countryToContinentMap.put(normalizedCountry, dto.getContinent());
            }
        }

        System.out.println("Extraction des continents terminée. " + countryToContinentMap.size() + " pays associés à des continents.");
    }

    /**
     * Création de tous les continents
     * Cette méthode doit être appelée après extractContinentData()
     */
    @Transactional
    public void createContinents(List<CovidDto> covidData) {
        System.out.println("Création des continents...");

        // Vérifier que la map countryToContinentMap n'est pas vide
        if (countryToContinentMap.isEmpty()) {
            System.out.println("Aucune donnée continent-pays disponible. Exécution de extractContinentData() d'abord.");
            extractContinentData(covidData);
        }

        // Utiliser les données du map countryToContinentMap déjà rempli
        Set<String> continents = countryToContinentMap.values().stream()
                .filter(c -> c != null && !c.trim().isEmpty())
                .collect(Collectors.toSet());

        for (String continentName : continents) {
            Continents continent = continentsRepository.findByName(continentName);
            
            if (continent == null) {
                continent = new Continents();
                continent.setName(continentName);
                continent = continentsRepository.save(continent);
                System.out.println("Continent créé: " + continentName + " avec ID: " + continent.getIdContinents());
            } else {
                System.out.println("Continent existant: " + continentName + " avec ID: " + continent.getIdContinents());
            }
            
            continentsMap.put(continentName, continent);
        }
        
        System.out.println(continentsMap.size() + " continents disponibles pour les associations.");
    }

    /**
     * Crée une région (pays) si elle n'existe pas déjà
     */
    private Regions createRegion(String countryName) {
        String normalizedName = DataNormalizationService.normalizeCountryName(countryName);
        if (normalizedName == null) return null;

        if (regionsMap.containsKey(normalizedName)) {
            return regionsMap.get(normalizedName);
        }

        Regions region = regionsRepository.findByName(normalizedName);
        if (region == null) {
            region = new Regions();
            region.setName(normalizedName);

            // Associer la région au continent correspondant
            String continentName = countryToContinentMap.get(normalizedName);
            if (continentName != null && continentsMap.containsKey(continentName)) {
                Continents continent = continentsMap.get(continentName);
                region.setContinent(continent);
                System.out.println("Région " + normalizedName + " associée au continent " + continentName);
            }

            region = regionsRepository.save(region);
            System.out.println("Région créée: " + normalizedName + " avec ID: " + region.getIdRegions());
        } else {
            // Mettre à jour le continent si nécessaire
            if (region.getContinent() == null) {
                String continentName = countryToContinentMap.get(normalizedName);
                if (continentName != null && continentsMap.containsKey(continentName)) {
                    Continents continent = continentsMap.get(continentName);
                    region.setContinent(continent);
                    region = regionsRepository.save(region);
                    System.out.println("Continent ajouté à la région existante: " + normalizedName);
                }
            }
            System.out.println("Région existante: " + normalizedName + " avec ID: " + region.getIdRegions());
        }

        regionsMap.put(normalizedName, region);
        return region;
    }

    /**
     * Préparation de la base de données avec toutes les régions nécessaires
     * Cette méthode doit être appelée après createContinents()
     */
    @Transactional
    public void prepareRegions(List<SarsDto> sarsData, List<CovidDto> covidData, List<CovidDailyDto> covidDailyData) {
        System.out.println("Préparation des régions (pays) dans la base de données...");

        // Ne pas appeler extractContinentData() ici - doit être fait avant
        if (countryToContinentMap.isEmpty()) {
            System.out.println("Attention: Aucune association pays-continent chargée. Les continents ne seront pas associés.");
        }

        if (continentsMap.isEmpty()) {
            System.out.println("Attention: Aucun continent chargé. Les continents ne seront pas associés.");
        }

        // Créer toutes les régions à partir des données SARS
        if (sarsData != null) {
            for (SarsDto dto : sarsData) {
                if (dto.getCountry() != null) {
                    createRegion(dto.getCountry());
                }
            }
        }

        // Créer toutes les régions à partir des données COVID
        if (covidData != null) {
            for (CovidDto dto : covidData) {
                if (dto.getCountry() != null) {
                    createRegion(dto.getCountry());
                }
            }
        }

        // Créer toutes les régions à partir des données COVID quotidiennes
        if (covidDailyData != null) {
            for (CovidDailyDto dto : covidDailyData) {
                if (dto.getCountry() != null) {
                    createRegion(dto.getCountry());
                }
            }
        }

        System.out.println("Préparation des régions terminée. " + regionsMap.size() + " régions créées ou trouvées.");
        
        // Vérification des associations continent-région
        long regionsWithContinent = regionsRepository.findAll().stream()
            .filter(r -> r.getContinent() != null)
            .count();
        
        System.out.println(regionsWithContinent + " régions ont une association avec un continent.");
    }

    /**
     * Insertion des données quotidiennes pour le SARS
     */
    @Transactional
    public void insertSarsData(List<SarsDto> sarsData) {
        if (sarsData == null || sarsData.isEmpty()) {
            System.out.println("Aucune donnée SARS à insérer.");
            return;
        }

        System.out.println("Insertion de " + sarsData.size() + " enregistrements SARS...");
        Pandemics sarsPandemic = pandemicsMap.get(DataNormalizationService.PANDEMIC_SARS);

        if (sarsPandemic == null) {
            System.err.println("Erreur: La pandémie SARS n'a pas été initialisée.");
            return;
        }

        int success = 0, failed = 0;

        // Trier les données par date
        List<SarsDto> sortedData = sarsData.stream()
                .sorted(Comparator.comparing(SarsDto::getDate))
                .collect(Collectors.toList());

        for (SarsDto dto : sortedData) {
            if (dto.getCountry() == null || dto.getDate() == null) {
                failed++;
                continue;
            }

            String normalizedCountry = DataNormalizationService.normalizeCountryName(dto.getCountry());
            Regions region = regionsMap.get(normalizedCountry);

            if (region != null) {
                try {
                    TotalByDay totalByDay = new TotalByDay();
                    totalByDay.setId(new TotalByDayId(sarsPandemic.getIdPandemics(),
                            region.getIdRegions(),
                            new Date(dto.getDate().getTime())));

                    totalByDay.setCaseCount(dto.getTotalCases());
                    totalByDay.setDeath(dto.getDeaths());
                    totalByDay.setRecovered(dto.getRecovered());
                    totalByDay.setPandemics(sarsPandemic);
                    totalByDay.setRegions(region);

                    totalByDayRepository.save(totalByDay);
                    success++;
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'insertion des données SARS pour " + dto.getCountry()
                            + " à la date " + dto.getDate() + ": " + e.getMessage());
                    failed++;
                }
            } else {
                System.err.println("Région non trouvée pour le pays: " + normalizedCountry);
                failed++;
            }
        }

        System.out.println("Insertion des données SARS terminée. Succès: " + success + ", Échecs: " + failed);
    }

    /**
     * Insertion des données quotidiennes pour le COVID-19
     */
    @Transactional
    public void insertCovidDailyData(List<CovidDailyDto> covidDailyData) {
        if (covidDailyData == null || covidDailyData.isEmpty()) {
            System.out.println("Aucune donnée COVID quotidienne à insérer.");
            return;
        }

        System.out.println("Insertion de " + covidDailyData.size() + " enregistrements COVID quotidiens...");
        Pandemics covidPandemic = pandemicsMap.get(DataNormalizationService.PANDEMIC_COVID);

        if (covidPandemic == null) {
            System.err.println("Erreur: La pandémie COVID-19 n'a pas été initialisée.");
            return;
        }

        int success = 0, failed = 0;

        // Trier les données par date
        List<CovidDailyDto> sortedData = covidDailyData.stream()
                .sorted(Comparator.comparing(CovidDailyDto::getDate))
                .collect(Collectors.toList());

        for (CovidDailyDto dto : sortedData) {
            if (dto.getCountry() == null || dto.getDate() == null) {
                failed++;
                continue;
            }

            String normalizedCountry = DataNormalizationService.normalizeCountryName(dto.getCountry());
            Regions region = regionsMap.get(normalizedCountry);

            if (region != null) {
                try {
                    TotalByDay totalByDay = new TotalByDay();
                    totalByDay.setId(new TotalByDayId(covidPandemic.getIdPandemics(),
                            region.getIdRegions(),
                            new Date(dto.getDate().getTime())));

                    // Traitement des valeurs potentiellement nulles
                    if (dto.getCumulativeTotalCases() != null) {
                        totalByDay.setCaseCount(dto.getCumulativeTotalCases().intValue());
                    } else {
                        totalByDay.setCaseCount(0);
                    }

                    if (dto.getCumulativeTotalDeaths() != null) {
                        totalByDay.setDeath(dto.getCumulativeTotalDeaths().intValue());
                    } else {
                        totalByDay.setDeath(0);
                    }

                    if (dto.getActiveCases() != null) {
                        totalByDay.setRecovered(dto.getActiveCases().intValue());
                    } else {
                        totalByDay.setRecovered(0);
                    }

                    totalByDay.setPandemics(covidPandemic);
                    totalByDay.setRegions(region);

                    totalByDayRepository.save(totalByDay);
                    success++;
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'insertion des données COVID pour " + dto.getCountry()
                            + " à la date " + dto.getDate() + ": " + e.getMessage());
                    failed++;
                }
            } else {
                System.err.println("Région non trouvée pour le pays: " + normalizedCountry);
                failed++;
            }
        }

        System.out.println("Insertion des données COVID quotidiennes terminée. Succès: " + success + ", Échecs: " + failed);
    }

    /**
     * Création des pays avec leurs associations de continent pour le front-end
     */
    @Transactional
    public void createCountriesWithContinents() {
        System.out.println("Création des pays avec leurs associations de continent...");
        
        int count = 0;
        for (Map.Entry<String, String> entry : countryToContinentMap.entrySet()) {
            String countryName = entry.getKey();
            String continentName = entry.getValue();

            if (continentName != null && continentsMap.containsKey(continentName)) {
                Continents continent = continentsMap.get(continentName);

                Countries country = countriesRepository.findByName(countryName);
                if (country == null) {
                    country = new Countries();
                    country.setName(countryName);
                    country.setContinents(continent);
                    country = countriesRepository.save(country);
                    countriesMap.put(countryName, country);
                    count++;
                } else if (country.getContinents() == null) {
                    country.setContinents(continent);
                    countriesRepository.save(country);
                    count++;
                }
            }
        }

        System.out.println(count + " pays créés ou mis à jour avec leurs associations de continent.");
    }

    /**
     * Méthode dépréciée - Utiliser extractContinentData(), createContinents() et createCountriesWithContinents() à la place
     */
    @Transactional
    @Deprecated
    public void createContinentCountriesRelations(List<CovidDto> covidData) {
        System.out.println("Cette méthode est dépréciée. Utiliser extractContinentData(), createContinents() et createCountriesWithContinents() à la place.");
        
        // Extraire les données de continent
        extractContinentData(covidData);
        
        // Créer les continents
        createContinents(covidData);
        
        // Créer les pays avec leurs associations continent
        createCountriesWithContinents();
    }

    /**
     * Analyse les données pour extraire des statistiques comparatives entre les pandémies
     */
    @Transactional(readOnly = true)
    public Map<String, Object> analyzeComparativePandemicData() {
        Map<String, Object> result = new HashMap<>();
        
        // Récupération des pandémies
        List<Pandemics> allPandemics = pandemicsRepository.findAll();
        result.put("pandemics", allPandemics);
        
        // Analyse par pandémie
        List<Map<String, Object>> pandemicStats = new ArrayList<>();
        for (Pandemics pandemic : allPandemics) {
            Map<String, Object> stats = new HashMap<>();
            stats.put("id", pandemic.getIdPandemics());
            stats.put("name", pandemic.getName());
            
            // Nombre total de cas par pandémie
            Long totalCases = totalByDayRepository.sumCasesByPandemic(pandemic.getIdPandemics());
            stats.put("totalCases", totalCases);
            
            // Nombre total de décès par pandémie
            Long totalDeaths = totalByDayRepository.sumDeathsByPandemic(pandemic.getIdPandemics());
            stats.put("totalDeaths", totalDeaths);
            
            // Calcul du taux de mortalité
            double mortalityRate = totalCases > 0 ? (totalDeaths.doubleValue() / totalCases.doubleValue()) * 100 : 0;
            stats.put("mortalityRate", mortalityRate);
            
            // Nombre de pays/régions touchés
            Long affectedRegions = totalByDayRepository.countDistinctRegionsByPandemic(pandemic.getIdPandemics());
            stats.put("affectedRegions", affectedRegions);
            
            pandemicStats.add(stats);
        }
        
        result.put("stats", pandemicStats);
        return result;
    }
}