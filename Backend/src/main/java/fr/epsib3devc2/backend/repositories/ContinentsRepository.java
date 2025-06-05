package fr.epsib3devc2.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.epsib3devc2.backend.bo.Continents;

@Repository
public interface ContinentsRepository extends JpaRepository<Continents, Integer> {
    Continents findByName(String name);
}
