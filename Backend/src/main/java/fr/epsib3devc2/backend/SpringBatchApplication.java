package fr.epsib3devc2.backend;

import fr.epsib3devc2.backend.dto.CovidDto;
import fr.epsib3devc2.backend.dto.CovidDailyDto;
import fr.epsib3devc2.backend.dto.SarsDto;
import fr.epsib3devc2.backend.repositories.*;
import fr.epsib3devc2.backend.services.DataInsertionService;
import fr.epsib3devc2.backend.services.ReadToCsv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class SpringBatchApplication implements CommandLineRunner {

    @Autowired
    private DataInsertionService dataInsertionService;

    @Autowired
    private ReadToCsv readToCsv;

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            System.out.println("===== DÉMARRAGE DU TRAITEMENT DES DONNÉES =====");
    
            // 1. Extraction des données des fichiers CSV
            List<SarsDto> sarsData = null;
            List<CovidDto> covidData = null;
            List<CovidDailyDto> covidDailyData = null;
            
            try {
                sarsData = readToCsv.readSarsData("src/Data/sars_2003_complete_dataset_clean.csv");
                covidData = readToCsv.readCovidData("src/Data/worldometer_coronavirus_summary_data.csv");
                covidDailyData = readToCsv.readCovidDailyData("src/Data/worldometer_coronavirus_daily_data.csv");
            } catch (Exception e) {
                System.err.println("Erreur lors de la lecture des fichiers CSV: " + e.getMessage());
            }
    
            // 2. Extraire les associations pays-continent
            dataInsertionService.extractContinentData(covidData);
            
            // 3. Créer les continents
            dataInsertionService.createContinents(covidData);
            
            // 4. Initialisation des pandémies
            dataInsertionService.initializeDatabase();
    
            // 5. Préparation des régions avec les associations continent
            dataInsertionService.prepareRegions(sarsData, covidData, covidDailyData);
    
            // 6. Insertion des données
            dataInsertionService.insertSarsData(sarsData);
            dataInsertionService.insertCovidDailyData(covidDailyData);
            
            // 7. Création des pays pour le front-end
            dataInsertionService.createCountriesWithContinents();
    
            System.out.println("===== TRAITEMENT DES DONNÉES TERMINÉ =====");
        } catch (Exception e) {
            System.err.println("Erreur générale: " + e.getMessage());
            e.printStackTrace();
        }
    }
}