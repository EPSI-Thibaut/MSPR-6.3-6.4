package fr.epsib3devc2.backend.bo;

import java.io.Serializable;

import jakarta.persistence.*;

@Entity
@Table(name = "countries")
public class Countries implements Serializable{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_countries")
  private Integer idCountries;
  
  @Column(name = "name")
  private String name;

  @ManyToOne
  @JoinColumn(name = "id_continents")
  private Continents continent;

  public Countries() {
  }

  public Integer getIdCountries() {
    return idCountries;
  }

  public void setIdCountries(Integer idCountries) {
    this.idCountries = idCountries;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "Countries{" +
        "idCountries=" + idCountries +
        ", name='" + name + '\'' +
        ", continent=" + (continent != null ? continent.getName() : "null") +
        '}';
  }

  public Continents getContinents() {
    return this.continent;
  }

  public void setContinents(Continents continent) {
    this.continent = continent;
  }
}