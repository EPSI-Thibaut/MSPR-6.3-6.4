package fr.epsib3devc2.backend.bo;

import java.io.Serializable;
import jakarta.persistence.*;

@Entity
@Table(name = "total_by_day")
public class TotalByDay implements Serializable {

  @EmbeddedId
  private TotalByDayId id;

  @Column(name = "caseCount")
  private Integer caseCount;

  @Column(name = "death")
  private Integer death;

  @Column(name = "recovered")
  private Integer recovered;

  @ManyToOne
  @MapsId("idPandemics")
  @JoinColumn(name = "id_pandemics")
  private Pandemics pandemics;

  @ManyToOne
  @MapsId("idRegions")
  @JoinColumn(name = "id_regions")
  private Regions regions;

  public TotalByDay() {
  }

  public TotalByDayId getId() {
    return id;
  }

  public void setId(TotalByDayId id) {
    this.id = id;
  }

  public Integer getCaseCount() {
    return caseCount;
  }

  public void setCaseCount(Integer caseCount) {
    this.caseCount = caseCount;
  }

  public Integer getDeath() {
    return death;
  }

  public void setDeath(Integer death) {
    this.death = death;
  }

  public Integer getRecovered() {
    return recovered;
  }

  public void setRecovered(Integer recovered) {
    this.recovered = recovered;
  }

  public Pandemics getPandemics() {
    return pandemics;
  }

  public void setPandemics(Pandemics pandemics) {
    this.pandemics = pandemics;
  }

  public Regions getRegions() {
    return regions;
  }

  public void setRegions(Regions regions) {
    this.regions = regions;
  }

  @Override
  public String toString() {
    return "TotalByDay{" +
            "caseCount=" + caseCount +
            ", death=" + death +
            ", recovered=" + recovered +
            '}';
  }
}