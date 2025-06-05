package fr.epsib3devc2.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.epsib3devc2.backend.bo.Continents;
import fr.epsib3devc2.backend.bo.Countries;

@Repository
public interface CountriesRepository extends JpaRepository<Countries, Integer> {
      Countries findByName(String name);

}
