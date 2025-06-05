package fr.epsib3devc2.backend.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvNumber;
import lombok.Data;

import java.util.Date;

@Data
public class CovidDailyDto {
    @CsvBindByName(column = "date")
    @CsvDate("yyyy-M-d")
    private Date date;

    @CsvBindByName(column = "country")
    private String country;

    @CsvBindByName(column = "cumulative_total_cases")
    @CsvNumber("###.#")
    private Double cumulativeTotalCases;

    @CsvBindByName(column = "daily_new_cases")
    private String dailyNewCases;

    @CsvBindByName(column = "active_cases")
    @CsvNumber("###.#")
    private Double activeCases;

    @CsvBindByName(column = "cumulative_total_deaths")
    @CsvNumber("###.#")
    private Double cumulativeTotalDeaths;

    @CsvBindByName(column = "daily_new_deaths")
    private String dailyNewDeaths;


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

    public Double getCumulativeTotalCases() {
        return cumulativeTotalCases;
    }

    public void setCumulativeTotalCases(Double cumulativeTotalCases) {
        this.cumulativeTotalCases = cumulativeTotalCases;
    }

    public String getDailyNewCases() {
        return dailyNewCases;
    }

    public void setDailyNewCases(String dailyNewCases) {
        this.dailyNewCases = dailyNewCases;
    }

    public Double getActiveCases() {
        return activeCases;
    }

    public void setActiveCases(Double activeCases) {
        this.activeCases = activeCases;
    }

    public Double getCumulativeTotalDeaths() {
        return cumulativeTotalDeaths;
    }

    public void setCumulativeTotalDeaths(Double cumulativeTotalDeaths) {
        this.cumulativeTotalDeaths = cumulativeTotalDeaths;
    }

    public String getDailyNewDeaths() {
        return dailyNewDeaths;
    }

    public void setDailyNewDeaths(String dailyNewDeaths) {
        this.dailyNewDeaths = dailyNewDeaths;
    }
}