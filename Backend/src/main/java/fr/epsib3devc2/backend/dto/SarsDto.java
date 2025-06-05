package fr.epsib3devc2.backend.dto;

import lombok.Data;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import java.util.Date;

@Data
public class SarsDto {
    @CsvBindByName(column = "Date")
    @CsvDate("yyyy-MM-dd")
    private Date date;

    @CsvBindByName(column = "Country")
    private String country;

    @CsvBindByName(column = "Cumulative number of case(s)")
    private int totalCases;

    @CsvBindByName(column = "Number of deaths")
    private int deaths;

    @CsvBindByName(column = "Number recovered")
    private int recovered;

    private int maleCases;
    private int femaleCases;
    private double fatalityRatio;
    private Date firstCaseDate;
    private Date lastCaseDate;
    private int medianAge;
    private String ageRange;
    private int importedCases;
    private double percentageImported;
    private int hcwAffected;
    private double percentageHcw;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getTotalCases() {
        return totalCases;
    }

    public void setTotalCases(int totalCases) {
        this.totalCases = totalCases;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getRecovered() {
        return recovered;
    }

    public void setRecovered(int recovered) {
        this.recovered = recovered;
    }

    public int getMaleCases() {
        return maleCases;
    }

    public void setMaleCases(int maleCases) {
        this.maleCases = maleCases;
    }

    public int getFemaleCases() {
        return femaleCases;
    }

    public void setFemaleCases(int femaleCases) {
        this.femaleCases = femaleCases;
    }

    public double getFatalityRatio() {
        return fatalityRatio;
    }

    public void setFatalityRatio(double fatalityRatio) {
        this.fatalityRatio = fatalityRatio;
    }

    public Date getFirstCaseDate() {
        return firstCaseDate;
    }

    public void setFirstCaseDate(Date firstCaseDate) {
        this.firstCaseDate = firstCaseDate;
    }

    public Date getLastCaseDate() {
        return lastCaseDate;
    }

    public void setLastCaseDate(Date lastCaseDate) {
        this.lastCaseDate = lastCaseDate;
    }

    public int getMedianAge() {
        return medianAge;
    }

    public void setMedianAge(int medianAge) {
        this.medianAge = medianAge;
    }

    public String getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(String ageRange) {
        this.ageRange = ageRange;
    }

    public int getImportedCases() {
        return importedCases;
    }

    public void setImportedCases(int importedCases) {
        this.importedCases = importedCases;
    }

    public double getPercentageImported() {
        return percentageImported;
    }

    public void setPercentageImported(double percentageImported) {
        this.percentageImported = percentageImported;
    }

    public int getHcwAffected() {
        return hcwAffected;
    }

    public void setHcwAffected(int hcwAffected) {
        this.hcwAffected = hcwAffected;
    }

    public double getPercentageHcw() {
        return percentageHcw;
    }

    public void setPercentageHcw(double percentageHcw) {
        this.percentageHcw = percentageHcw;
    }
}
