package fr.epsib3devc2.backend.repositories;

import fr.epsib3devc2.backend.bo.CovidPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PredictionCovidRepository extends JpaRepository<CovidPrediction, Long> {
    // Méthodes personnalisées si nécessaire
}