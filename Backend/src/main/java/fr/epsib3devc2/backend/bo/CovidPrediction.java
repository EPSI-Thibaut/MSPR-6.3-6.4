package fr.epsib3devc2.backend.bo;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

// Class pour la table de prédictions de Covid qui sera remplie par l'API Flask
@Entity
@Table(name = "covid_predictions")
public class CovidPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prediction_date", nullable = false)
    private LocalDate predictionDate;

    @Column(name = "region_id")
    private Long regionId;

    @Column(name = "region_name")
    private String regionName;

    @Column(name = "continent_name")
    private String continentName;

    @Column(name = "predicted_cases", nullable = false)
    private Long predictedCases;

    @Column(name = "predicted_deaths")
    private Long predictedDeaths;

    @Column(name = "predicted_recovered")
    private Long predictedRecovered;

    @Column(name = "model_version")
    private String modelVersion;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getPredictionDate() {
        return predictionDate;
    }

    public void setPredictionDate(LocalDate predictionDate) {
        this.predictionDate = predictionDate;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getContinentName() {
        return continentName;
    }

    public void setContinentName(String continentName) {
        this.continentName = continentName;
    }

    public Long getPredictedCases() {
        return predictedCases;
    }

    public void setPredictedCases(Long predictedCases) {
        this.predictedCases = predictedCases;
    }

    public Long getPredictedDeaths() {
        return predictedDeaths;
    }

    public void setPredictedDeaths(Long predictedDeaths) {
        this.predictedDeaths = predictedDeaths;
    }

    public Long getPredictedRecovered() {
        return predictedRecovered;
    }

    public void setPredictedRecovered(Long predictedRecovered) {
        this.predictedRecovered = predictedRecovered;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}