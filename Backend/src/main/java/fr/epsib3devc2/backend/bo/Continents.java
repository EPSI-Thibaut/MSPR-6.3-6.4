package fr.epsib3devc2.backend.bo;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "continents")
public class Continents implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_continents")
  private Integer idContinents;
  @Column(name = "name")
  private String name;

  @OneToMany(mappedBy = "continent")
  private List<Countries> countries;
  @OneToMany(mappedBy = "continent")
  private List<Regions> regions;

  public Continents() {
  }

  public Integer getIdContinents() {
    return idContinents;
  }

  public void setIdContinents(Integer idContinents) {
    this.idContinents = idContinents;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "Continent{" +
        "idContinents=" + idContinents +
        ", name='" + name + '\'' +
        '}';
  }
}
