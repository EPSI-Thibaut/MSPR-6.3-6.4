package fr.epsib3devc2.backend.repositories;

import fr.epsib3devc2.backend.bo.TotalByDay;
import fr.epsib3devc2.backend.bo.TotalByDayId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TotalByDayRepository extends JpaRepository<TotalByDay, TotalByDayId> {

    @Query("SELECT t FROM TotalByDay t WHERE t.pandemics.idPandemics = :pandemicId AND t.regions.idRegions = :regionId ORDER BY t.id.date DESC")
    List<TotalByDay> findLatestByPandemicAndRegion(@Param("pandemicId") long pandemicId, @Param("regionId") Integer regionId);

    @Query("SELECT t FROM TotalByDay t WHERE t.pandemics.idPandemics = :pandemicId AND t.regions.idRegions = :regionId ORDER BY t.id.date ASC")
    List<TotalByDay> findByPandemicAndRegionOrderByDate(@Param("pandemicId") Integer pandemicId, @Param("regionId") Integer regionId);

    @Query("SELECT SUM(t.caseCount) FROM TotalByDay t WHERE t.pandemics.idPandemics = :pandemicId")
    Long sumCasesByPandemic(@Param("pandemicId") Integer pandemicId);

    @Query("SELECT SUM(t.death) FROM TotalByDay t WHERE t.pandemics.idPandemics = :pandemicId")
    Long sumDeathsByPandemic(@Param("pandemicId") Integer pandemicId);
    
    @Query("SELECT COUNT(DISTINCT t.regions.idRegions) FROM TotalByDay t WHERE t.pandemics.idPandemics = :pandemicId")
    Long countDistinctRegionsByPandemic(@Param("pandemicId") Integer pandemicId);
    
    @Query("SELECT SUM(t.caseCount) FROM TotalByDay t WHERE t.pandemics.idPandemics = :pandemicId AND t.regions.continent.idContinents = :continentId")
    Long sumCasesByPandemicAndContinent(@Param("pandemicId") Integer pandemicId, @Param("continentId") Integer continentId);
    
    @Query("SELECT SUM(t.death) FROM TotalByDay t WHERE t.pandemics.idPandemics = :pandemicId AND t.regions.continent.idContinents = :continentId")
    Long sumDeathsByPandemicAndContinent(@Param("pandemicId") Integer pandemicId, @Param("continentId") Integer continentId);
    
    @Query("SELECT COUNT(t) FROM TotalByDay t WHERE t.regions.idRegions = :regionId")
    Long countByRegionId(@Param("regionId") Integer regionId);
    
}