package fr.epsib3devc2.backend.services;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvValidationException;
import fr.epsib3devc2.backend.dto.CovidDto;
import fr.epsib3devc2.backend.dto.CovidDailyDto;
import fr.epsib3devc2.backend.dto.SarsDto;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReadToCsv {

    public static List<String> readAllLines(String path) throws IOException {
        Path filePath = Paths.get(path);
        verifyFile(filePath);
        
        List<String> lines = Files.readAllLines(filePath);
        System.out.println("Nombre total de lignes dans le fichier " + path + ": " + lines.size());
        System.out.println("Première ligne: " + lines.get(0));
        return lines;
    }

    private static void verifyFile(Path filePath) {
        File file = filePath.toFile();
        System.out.println("\nVérification du fichier: " + filePath.toAbsolutePath());
        System.out.println("Le fichier existe: " + file.exists());
        System.out.println("Le fichier est lisible: " + file.canRead());
        System.out.println("Taille du fichier: " + file.length() + " octets");
    }

    private void displayCsvHeaders(String path, String fileType) throws IOException {
        Path filePath = Paths.get(path);
        verifyFile(filePath);
        
        try (CSVReader reader = new CSVReader(new FileReader(path))) {
            String[] headers = reader.readNext();
            if (headers != null) {
                System.out.println(fileType + " CSV Headers (" + headers.length + "): " + Arrays.toString(headers));
                
                String[] firstRow = reader.readNext();
                if (firstRow != null) {
                    System.out.println(fileType + " First Data Row (" + firstRow.length + "): " + Arrays.toString(firstRow));
                    
                    System.out.println(fileType + " Headers-Values Pairing:");
                    for (int i = 0; i < Math.min(headers.length, firstRow.length); i++) {
                        System.out.println("  " + headers[i] + " = " + firstRow[i]);
                    }
                }
            }
        } catch (CsvValidationException e) {
            System.err.println("Erreur de validation CSV: " + e.getMessage());
        }
    }

    public List<SarsDto> readSarsData(String path) throws IOException {
        System.out.println("\n=== LECTURE DES DONNÉES SARS ===");
        displayCsvHeaders(path, "SARS");

        try (FileReader reader = new FileReader(path)) {
            HeaderColumnNameMappingStrategy<SarsDto> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(SarsDto.class);
            
            List<SarsDto> data = new CsvToBeanBuilder<SarsDto>(reader)
                    .withMappingStrategy(strategy)
                    .withSeparator(',')
                    .withSkipLines(0)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build()
                    .parse();

            // Validation et nettoyage des données
            List<SarsDto> validData = data.stream()
                    .filter(dto -> dto.getCountry() != null && !dto.getCountry().isEmpty())
                    .collect(Collectors.toList());

            System.out.println("Total des enregistrements SARS lus: " + data.size());
            System.out.println("Enregistrements SARS valides: " + validData.size());
            
            if (!validData.isEmpty()) {
                SarsDto first = validData.get(0);
                System.out.println("Premier SarsDto valide: ");
                System.out.println("  date = " + first.getDate());
                System.out.println("  country = " + first.getCountry());
                System.out.println("  totalCases = " + first.getTotalCases());
                System.out.println("  deaths = " + first.getDeaths());
                System.out.println("  recovered = " + first.getRecovered());
            }
            
            return validData;
        } catch (Exception e) {
            System.err.println("Erreur lors du parsing des données SARS: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Échec du parsing des données SARS", e);
        }
    }

    public List<CovidDto> readCovidData(String path) throws IOException {
        System.out.println("\n=== LECTURE DES DONNÉES COVID SUMMARY ===");
        displayCsvHeaders(path, "COVID Summary");

        try (FileReader reader = new FileReader(path)) {
            HeaderColumnNameMappingStrategy<CovidDto> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(CovidDto.class);
            
            List<CovidDto> data = new CsvToBeanBuilder<CovidDto>(reader)
                    .withMappingStrategy(strategy)
                    .withSeparator(',')
                    .withSkipLines(0)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build()
                    .parse();

            // Validation et nettoyage des données
            List<CovidDto> validData = data.stream()
                    .filter(dto -> dto.getCountry() != null && !dto.getCountry().isEmpty())
                    .collect(Collectors.toList());

            System.out.println("Total des enregistrements COVID lus: " + data.size());
            System.out.println("Enregistrements COVID valides: " + validData.size());
            
            if (!validData.isEmpty()) {
                CovidDto first = validData.get(0);
                System.out.println("Premier CovidDto valide: ");
                System.out.println("  country = " + first.getCountry());
                System.out.println("  continent = " + first.getContinent());
                System.out.println("  totalConfirmed = " + first.getTotalConfirmed());
                System.out.println("  totalDeaths = " + first.getTotalDeaths());
                System.out.println("  totalRecovered = " + first.getTotalRecovered());
            }
            
            return validData;
        } catch (Exception e) {
            System.err.println("Erreur lors du parsing des données COVID: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Échec du parsing des données COVID", e);
        }
    }

    public List<CovidDailyDto> readCovidDailyData(String path) throws IOException {
        System.out.println("\n=== LECTURE DES DONNÉES COVID DAILY ===");
        displayCsvHeaders(path, "COVID Daily");

        try (FileReader reader = new FileReader(path)) {
            HeaderColumnNameMappingStrategy<CovidDailyDto> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(CovidDailyDto.class);
            
            List<CovidDailyDto> data = new CsvToBeanBuilder<CovidDailyDto>(reader)
                    .withMappingStrategy(strategy)
                    .withSeparator(',')
                    .withSkipLines(0) // Ne pas sauter l'en-tête
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build()
                    .parse();

            // Validation et nettoyage des données
            List<CovidDailyDto> validData = data.stream()
                    .filter(dto -> dto.getCountry() != null && !dto.getCountry().isEmpty() && dto.getDate() != null)
                    .collect(Collectors.toList());

            System.out.println("Total des enregistrements COVID Daily lus: " + data.size());
            System.out.println("Enregistrements COVID Daily valides: " + validData.size());
            
            if (!validData.isEmpty()) {
                CovidDailyDto first = validData.get(0);
                System.out.println("Premier CovidDailyDto valide: ");
                System.out.println("  date = " + first.getDate());
                System.out.println("  country = " + first.getCountry());
                System.out.println("  cumulativeTotalCases = " + first.getCumulativeTotalCases());
                System.out.println("  dailyNewCases = " + first.getDailyNewCases());
                System.out.println("  activeCases = " + first.getActiveCases());
                System.out.println("  cumulativeTotalDeaths = " + first.getCumulativeTotalDeaths());
                System.out.println("  dailyNewDeaths = " + first.getDailyNewDeaths());
            }
            
            return validData;
        } catch (Exception e) {
            System.err.println("Erreur lors du parsing des données COVID Daily: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Échec du parsing des données COVID Daily", e);
        }
    }
}