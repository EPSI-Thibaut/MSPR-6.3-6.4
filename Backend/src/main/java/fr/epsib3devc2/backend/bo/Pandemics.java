package fr.epsib3devc2.backend.bo;

import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "pandemics")
public class Pandemics implements Serializable{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_pandemics")
  private Integer idPandemics;
  @Column(name = "name")
  private String name;

  @OneToMany(mappedBy = "pandemics")
  private Set<TotalByDay> totalByDays;

  public Pandemics() {
  }

  public Set<TotalByDay> getTotalByDays() {
    return totalByDays;
}

  public void setTotalByDays(Set<TotalByDay> totalByDays) {
      this.totalByDays = totalByDays;
  }

  public Integer getIdPandemics() {
    return idPandemics;
  }

  public void setIdPandemics(Integer idPandemics) {
    this.idPandemics = idPandemics;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "pandemics{" +
        "idPandemics=" + idPandemics +
        ", name='" + name + '\'' +
        '}';
  }
}
