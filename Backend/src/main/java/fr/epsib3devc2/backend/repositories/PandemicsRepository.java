package fr.epsib3devc2.backend.repositories;

import fr.epsib3devc2.backend.bo.Pandemics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PandemicsRepository extends JpaRepository<Pandemics, Long> {
    List<Pandemics> findByName(String name);
}