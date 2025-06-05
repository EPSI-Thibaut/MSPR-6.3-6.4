package fr.epsib3devc2.backend.bo;

import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "regions")
public class Regions implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_regions")
  private Integer idRegions;
  
  @Column(name = "name")
  private String name;

  @OneToMany(mappedBy = "regions")
  private Set<TotalByDay> totalByDays;

  @ManyToOne
  @JoinColumn(name = "id_continents")
  private Continents continent;

  public Regions() {
  }

  public Set<TotalByDay> getTotalByDays() {
    return totalByDays;
  }

  public void setTotalByDays(Set<TotalByDay> totalByDays) {
    this.totalByDays = totalByDays;
  }

  public Integer getIdRegions() {
    return idRegions;
  }

  public void setIdRegions(Integer idRegions) {
    this.idRegions = idRegions;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  // Correction du nom de la méthode pour correspondre au nom du champ
  public Continents getContinent() {
    return this.continent;
  }

  // Correction du nom de la méthode pour correspondre au nom du champ
  public void setContinent(Continents continent) {
    this.continent = continent;
  }

  @Override
  public String toString() {
    return "Regions{" +
            "id_regions=" + idRegions +
            ", name='" + name + '\'' +
            ", continent=" + (continent != null ? continent.getName() : "null") +
            '}';
  }
}