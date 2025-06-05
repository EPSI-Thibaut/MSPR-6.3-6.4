package fr.epsib3devc2.backend.dto;

import lombok.Data;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvNumber;

@Data
public class CovidDto {
    @CsvBindByName(column = "country")
    private String country;

    @CsvBindByName(column = "continent")
    private String continent;

    @CsvBindByName(column = "total_confirmed")
    private Integer totalConfirmed;

    @CsvBindByName(column = "total_deaths")
    private Double totalDeaths;

    @CsvBindByName(column = "total_recovered")
    private Double totalRecovered;

    @CsvBindByName(column = "active_cases")
    private Double activeCases;

    @CsvBindByName(column = "serious_or_critical")
    private Double seriousOrCritical;

    @CsvBindByName(column = "total_cases_per_1m_population")
    private Double casesPerMillion;

    @CsvBindByName(column = "total_deaths_per_1m_population")
    private Double deathsPerMillion;

    @CsvBindByName(column = "total_tests")
    private Double totalTests;

    @CsvBindByName(column = "total_tests_per_1m_population")
    private Double testsPerMillion;

    @CsvBindByName(column = "population")
    private Long population;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public Integer getTotalConfirmed() {
        return totalConfirmed;
    }

    public void setTotalConfirmed(Integer totalConfirmed) {
        this.totalConfirmed = totalConfirmed;
    }

    public Double getTotalDeaths() {
        return totalDeaths;
    }

    public void setTotalDeaths(Double totalDeaths) {
        this.totalDeaths = totalDeaths;
    }

    public Double getTotalRecovered() {
        return totalRecovered;
    }

    public void setTotalRecovered(Double totalRecovered) {
        this.totalRecovered = totalRecovered;
    }

    public Double getActiveCases() {
        return activeCases;
    }

    public void setActiveCases(Double activeCases) {
        this.activeCases = activeCases;
    }

    public Double getSeriousOrCritical() {
        return seriousOrCritical;
    }

    public void setSeriousOrCritical(Double seriousOrCritical) {
        this.seriousOrCritical = seriousOrCritical;
    }

    public Double getCasesPerMillion() {
        return casesPerMillion;
    }

    public void setCasesPerMillion(Double casesPerMillion) {
        this.casesPerMillion = casesPerMillion;
    }

    public Double getDeathsPerMillion() {
        return deathsPerMillion;
    }

    public void setDeathsPerMillion(Double deathsPerMillion) {
        this.deathsPerMillion = deathsPerMillion;
    }

    public Double getTotalTests() {
        return totalTests;
    }

    public void setTotalTests(Double totalTests) {
        this.totalTests = totalTests;
    }

    public Double getTestsPerMillion() {
        return testsPerMillion;
    }

    public void setTestsPerMillion(Double testsPerMillion) {
        this.testsPerMillion = testsPerMillion;
    }

    public Long getPopulation() {
        return population;
    }

    public void setPopulation(Long population) {
        this.population = population;
    }
}