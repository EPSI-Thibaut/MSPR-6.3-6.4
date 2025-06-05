package fr.epsib3devc2.backend.bo;

import java.io.Serializable;
import java.sql.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class TotalByDayId implements Serializable {

  @Column(name = "id_pandemics")
  private Integer idPandemics;

  @Column(name = "id_regions")
  private Integer idRegions;

  @Column(name = "date_by_day")
  private Date date;

  public TotalByDayId() {
  }
  
  public TotalByDayId(Integer idPandemics, Integer idRegions, Date date) {
    this.idPandemics = idPandemics;
    this.idRegions = idRegions;
    this.date = date;
  }

  // Getters et Setters
  public Integer getIdPandemics() {
    return idPandemics;
  }

  public void setIdPandemics(Integer idPandemics) {
    this.idPandemics = idPandemics;
  }

  public Integer getIdRegions() {
    return idRegions;
  }

  public void setIdRegions(Integer idRegions) {
    this.idRegions = idRegions;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TotalByDayId that = (TotalByDayId) o;

    if (!idPandemics.equals(that.idPandemics)) return false;
    if (!idRegions.equals(that.idRegions)) return false;
    return date.equals(that.date);
  }

  @Override
  public int hashCode() {
    int result = idPandemics.hashCode();
    result = 31 * result + idRegions.hashCode();
    result = 31 * result + date.hashCode();
    return result;
  }
  
  @Override
  public String toString() {
    return "TotalByDayId{idPandemics=" + idPandemics + ", idRegions=" + idRegions + ", date=" + date + '}';
  }
}