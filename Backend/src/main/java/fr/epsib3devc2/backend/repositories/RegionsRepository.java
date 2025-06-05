package fr.epsib3devc2.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.epsib3devc2.backend.bo.Continents;
import fr.epsib3devc2.backend.bo.Regions;

@Repository
public interface RegionsRepository extends JpaRepository<Regions, Integer> {

    Regions findByName(String country);
    List<Regions> findByContinent(Continents continent);
}
