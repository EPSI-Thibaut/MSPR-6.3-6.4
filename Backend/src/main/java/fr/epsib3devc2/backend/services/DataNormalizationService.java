package fr.epsib3devc2.backend.services;

import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

/**
 * Service pour normaliser les données des fichiers CSV avant insertion en BDD
 */
public class DataNormalizationService {
    
 // Types de pandémies à insérer dans la table pandemics
    public static final String PANDEMIC_COVID = "COVID";
    public static final String PANDEMIC_SARS = "SARS";
    
    // Mapping pour normaliser les noms de pays
    private static final Map<String, String> COUNTRY_NAME_MAPPING = new HashMap<>();
    
    static {
        COUNTRY_NAME_MAPPING.put("US", "United States");
        COUNTRY_NAME_MAPPING.put("USA", "United States");
        COUNTRY_NAME_MAPPING.put("United States of America", "United States");
        COUNTRY_NAME_MAPPING.put("UK", "United Kingdom");
        COUNTRY_NAME_MAPPING.put("UAE", "United Arab Emirates");
        COUNTRY_NAME_MAPPING.put("South Korea", "Korea, South");
    }
    
    // Ensemble de continents valides
    private static final Set<String> VALID_CONTINENTS = new HashSet<>();
    
    static {
        VALID_CONTINENTS.add("Africa");
        VALID_CONTINENTS.add("Asia");
        VALID_CONTINENTS.add("Europe");
        VALID_CONTINENTS.add("North America");
        VALID_CONTINENTS.add("South America");
        VALID_CONTINENTS.add("Oceania");
        VALID_CONTINENTS.add("Antarctica");
    }
    
    /**
     * Normalise le nom d'un pays pour assurer la cohérence entre les fichiers
     */
    public static String normalizeCountryName(String countryName) {
        if (countryName == null || countryName.trim().isEmpty()) {
            return null;
        }
        
        String trimmed = countryName.trim();
        return COUNTRY_NAME_MAPPING.getOrDefault(trimmed, trimmed);
    }
    
    /**
     * Vérifie si un continent est valide
     */
    public static boolean isValidContinent(String continent) {
        return continent != null && VALID_CONTINENTS.contains(continent.trim());
    }
    
    /**
     * Obtenir le type de pandémie pour le nom du fichier
     */
    public static String determinePandemicType(String fileName) {
        if (fileName == null) {
            return PANDEMIC_COVID; // Par défaut
        }
        
        String lowerCase = fileName.toLowerCase();
        if (lowerCase.contains("sars")) {
            return PANDEMIC_SARS;
        } else {
            return PANDEMIC_COVID;
        }
    }
}